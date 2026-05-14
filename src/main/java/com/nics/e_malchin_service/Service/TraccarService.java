package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.GpsPosition;
import com.nics.e_malchin_service.repository.GpsPositionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TraccarService {

    @Value("${traccar.url}")
    private String traccarUrl;

    @Value("${traccar.email}")
    private String traccarEmail;

    @Value("${traccar.password}")
    private String traccarPassword;

    private static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter ISO_PLAIN =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final RestTemplate restTemplate = new RestTemplate();
    private final GpsPositionRepository positionRepository;

    public TraccarService(GpsPositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    private HttpHeaders authHeaders() {
        String credentials = traccarEmail + ":" + traccarPassword;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public List<?> getDevices() {
        URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/devices").build().toUri();
        ResponseEntity<List> response = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    // Returns latest position per IMEI from NICS DB, falls back to Traccar API if DB is empty
    public List<Map<String, Object>> getLatestPositions() {
        List<GpsPosition> dbPositions = positionRepository.findLatestPerImei();
        if (!dbPositions.isEmpty()) {
            return toMapList(dbPositions);
        }
        // fallback: live call to Traccar
        URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/positions").build().toUri();
        ResponseEntity<List> response = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    // Queries route from NICS DB (populated by GpsPositionSyncService)
    public List<Map<String, Object>> getRoute(Integer deviceId, String from, String to) {
        String imei = resolveImei(deviceId);
        if (imei == null) return Collections.emptyList();

        LocalDateTime fromDt = parseDateTime(from);
        LocalDateTime toDt   = parseDateTime(to);

        List<GpsPosition> positions =
                positionRepository.findByImeiAndFixTimeBetweenOrderByFixTimeAsc(imei, fromDt, toDt);
        return toMapList(positions);
    }

    @SuppressWarnings("unchecked")
    private String resolveImei(Integer deviceId) {
        try {
            List<?> devices = getDevices();
            for (Object d : devices) {
                Map<String, Object> device = (Map<String, Object>) d;
                Object id = device.get("id");
                if (id != null && Integer.valueOf(id.toString()).equals(deviceId)) {
                    return (String) device.get("uniqueId");
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private List<Map<String, Object>> toMapList(List<GpsPosition> positions) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GpsPosition p : positions) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("fixTime",   p.getFixTime().format(ISO_PLAIN) + "Z");
            m.put("latitude",  p.getLatitude());
            m.put("longitude", p.getLongitude());
            m.put("speed",     p.getSpeed());
            m.put("course",    p.getCourse());
            m.put("altitude",  p.getAltitude());
            m.put("deviceId",  p.getImei());
            result.add(m);
        }
        return result;
    }

    private LocalDateTime parseDateTime(String dt) {
        // Accept both "2026-05-01T00:00:00.000Z" and "2026-05-01T00:00:00Z"
        String normalized = dt.endsWith("Z") ? dt.substring(0, dt.length() - 1) : dt;
        if (normalized.contains(".")) {
            normalized = normalized.substring(0, normalized.indexOf('.'));
        }
        return LocalDateTime.parse(normalized, ISO_PLAIN);
    }
}
