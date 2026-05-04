package com.nics.e_malchin_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TraccarService {

    @Value("${traccar.url}")
    private String traccarUrl;

    @Value("${traccar.email}")
    private String traccarEmail;

    @Value("${traccar.password}")
    private String traccarPassword;

    @Value("${traccar.log.path:/traccar-logs/tracker-server.log}")
    private String logPath;

    private final RestTemplate restTemplate = new RestTemplate();

    // Matches: "2026-05-04 10:48:03  INFO: [...: tk103 < ...] S168#IMEI#...#LOCA:G;...GDATA:A,sats,YYMMDDHHMMSS,lat,lon,speed,heading,alt"
    private static final Pattern GDATA_PATTERN = Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*?#(\\d{15})#.*?GDATA:A,(\\d+),(\\d{12}),([-\\d.]+),([-\\d.]+),(\\d+),(\\d+),(\\d+)"
    );
    private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    public List<?> getLatestPositions() {
        URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/positions").build().toUri();
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    // Resolves deviceId → IMEI by querying Traccar devices, then parses log file for GDATA:A positions
    public List<Map<String, Object>> getRoute(Integer deviceId, String from, String to) {
        String imei = resolveImei(deviceId);
        if (imei == null) return Collections.emptyList();
        return parseLogForPositions(imei, from, to);
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

    private List<Map<String, Object>> parseLogForPositions(String imei, String from, String to) {
        List<Map<String, Object>> positions = new ArrayList<>();
        LocalDateTime fromDt = LocalDateTime.parse(from, ISO_UTC);
        LocalDateTime toDt   = LocalDateTime.parse(to,   ISO_UTC);

        try (BufferedReader reader = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(imei) || !line.contains("GDATA:A")) continue;
                Matcher m = GDATA_PATTERN.matcher(line);
                if (!m.find()) continue;

                LocalDateTime logTime = LocalDateTime.parse(m.group(1), LOG_TS);
                if (logTime.isBefore(fromDt) || logTime.isAfter(toDt)) continue;

                Map<String, Object> pos = new LinkedHashMap<>();
                pos.put("fixTime",   logTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z");
                pos.put("latitude",  Double.parseDouble(m.group(5)));
                pos.put("longitude", Double.parseDouble(m.group(6)));
                pos.put("speed",     Double.parseDouble(m.group(7)));
                pos.put("course",    Double.parseDouble(m.group(8)));
                pos.put("deviceId",  imei);
                positions.add(pos);
            }
        } catch (Exception e) {
            System.err.println("TraccarService log parse error: " + e.getMessage());
        }

        return positions;
    }
}
