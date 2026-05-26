package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.repository.GpsPositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Pulls GPS positions from the Traccar HTTP API and stores them in gps_position.
 *
 * Flow every sync interval:
 *   1. GET /api/devices  → build traccarId → IMEI map
 *   2. GET /api/reports/route?deviceId=X&from=...&to=...  per device
 *   3. Batch INSERT IGNORE into gps_position (unique constraint deduplicates)
 */
@Service
public class GpsPositionSyncService {

    private static final Logger log = LoggerFactory.getLogger(GpsPositionSyncService.class);

    @Value("${traccar.url}")
    private String traccarUrl;

    @Value("${traccar.email}")
    private String traccarEmail;

    @Value("${traccar.password}")
    private String traccarPassword;

    @Value("${traccar.sync.interval-ms:60000}")
    private long syncIntervalMs;

    @Value("${traccar.sync.history-minutes:120}")
    private int historyMinutes;

    private final GpsPositionRepository positionRepo;
    private final JdbcTemplate jdbc;
    private final RestTemplate restTemplate = new RestTemplate();

    public GpsPositionSyncService(GpsPositionRepository positionRepo, JdbcTemplate jdbc) {
        this.positionRepo = positionRepo;
        this.jdbc         = jdbc;
    }

    // ─── Scheduled sync ──────────────────────────────────────────────────────

    @Scheduled(fixedDelayString = "${traccar.sync.interval-ms:60000}")
    public int syncFromTraccar() {
        Map<Integer, String> deviceMap = fetchTraccarDevices();
        if (deviceMap.isEmpty()) {
            log.warn("GPS sync: no devices returned from Traccar — check traccar.url / credentials");
            return 0;
        }

        // Rolling window: start from (latest known fix - 5 min) or (now - historyMinutes),
        // whichever is more recent, so we fill gaps without re-fetching all history.
        LocalDateTime to      = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime from    = to.minusMinutes(historyMinutes);
        Optional<LocalDateTime> lastKnown = positionRepo.findMaxFixTime();
        if (lastKnown.isPresent() && lastKnown.get().isAfter(from.plusMinutes(5))) {
            from = lastKnown.get().minusMinutes(5);
        }

        String fromStr = from.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String toStr   = to.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        int saved = 0;
        for (Map.Entry<Integer, String> entry : deviceMap.entrySet()) {
            try {
                saved += fetchAndStore(entry.getKey(), entry.getValue(), fromStr, toStr);
            } catch (Exception e) {
                log.warn("GPS sync: skipped device {} ({}): {}", entry.getKey(), entry.getValue(), e.getMessage());
            }
        }

        if (saved > 0) log.info("GPS sync done — saved {} new positions", saved);
        return saved;
    }

    // ─── Traccar API calls ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<Integer, String> fetchTraccarDevices() {
        Map<Integer, String> result = new LinkedHashMap<>();
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/devices").build().toUri();
            ResponseEntity<List> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
            if (resp.getBody() == null) return result;
            for (Object item : resp.getBody()) {
                Map<String, Object> d = (Map<String, Object>) item;
                Object id  = d.get("id");
                Object uid = d.get("uniqueId");
                if (id != null && uid != null) {
                    result.put(((Number) id).intValue(), uid.toString().trim());
                }
            }
        } catch (Exception e) {
            log.error("GPS sync: failed to fetch Traccar devices: {}", e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private int fetchAndStore(int traccarDeviceId, String imei, String from, String to) {
        URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/reports/route")
                .queryParam("deviceId", traccarDeviceId)
                .queryParam("from", from)
                .queryParam("to", to)
                .build().toUri();

        ResponseEntity<List> resp = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        if (resp.getBody() == null || resp.getBody().isEmpty()) return 0;

        List<Object[]> batch = new ArrayList<>();
        for (Object item : resp.getBody()) {
            Map<String, Object> pos = (Map<String, Object>) item;
            Object[] row = mapToRow(pos, imei);
            if (row != null) batch.add(row);
        }
        if (batch.isEmpty()) return 0;

        int[] results = jdbc.batchUpdate(
                "INSERT IGNORE INTO gps_position " +
                "(imei, latitude, longitude, speed, course, altitude, fix_time, synced_at, sats, battery_volt, signal_level) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                batch);
        return Arrays.stream(results).sum();
    }

    // ─── Mapping ─────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Object[] mapToRow(Map<String, Object> pos, String imei) {
        // fixTime: prefer "fixTime", fall back to "deviceTime"
        String fixTimeStr = (String) pos.getOrDefault("fixTime", pos.get("deviceTime"));
        if (fixTimeStr == null) return null;

        LocalDateTime fixTime;
        try {
            fixTime = OffsetDateTime.parse(fixTimeStr).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }

        Object lat = pos.get("latitude");
        Object lon = pos.get("longitude");
        if (lat == null || lon == null) return null;

        Map<String, Object> attrs = pos.containsKey("attributes")
                ? (Map<String, Object>) pos.get("attributes")
                : Collections.emptyMap();

        Double batteryVolt = extractBattery(attrs);
        Integer signalLevel = extractSignal(attrs);
        Integer sats = extractInt(attrs, "sat", "satellites");

        return new Object[]{
                imei,
                ((Number) lat).doubleValue(),
                ((Number) lon).doubleValue(),
                getDouble(pos, "speed"),
                getDouble(pos, "course"),
                getDouble(pos, "altitude"),
                fixTime,
                LocalDateTime.now(ZoneOffset.UTC),
                sats,
                batteryVolt,
                signalLevel
        };
    }

    private Double extractBattery(Map<String, Object> attrs) {
        // "power" = raw voltage from tracker
        Object power = attrs.get("power");
        if (power != null) return ((Number) power).doubleValue();
        // "batteryLevel" = percentage (0-100), approximate to voltage range 3.5-4.2V
        Object pct = attrs.get("batteryLevel");
        if (pct != null) return 3.5 + ((Number) pct).doubleValue() / 100.0 * 0.7;
        return null;
    }

    private Integer extractSignal(Map<String, Object> attrs) {
        for (String key : new String[]{"rssi", "gsm", "signalStrength", "signalLevel"}) {
            Object v = attrs.get(key);
            if (v != null) return ((Number) v).intValue();
        }
        return null;
    }

    private Integer extractInt(Map<String, Object> attrs, String... keys) {
        for (String key : keys) {
            Object v = attrs.get(key);
            if (v != null) return ((Number) v).intValue();
        }
        return null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? ((Number) v).doubleValue() : null;
    }

    private HttpHeaders authHeaders() {
        String encoded = Base64.getEncoder().encodeToString(
                (traccarEmail + ":" + traccarPassword).getBytes(StandardCharsets.UTF_8));
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "Basic " + encoded);
        h.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return h;
    }
}
