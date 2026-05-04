package com.nics.e_malchin_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class TraccarService {

    @Value("${traccar.url}")
    private String traccarUrl;

    @Value("${traccar.email}")
    private String traccarEmail;

    @Value("${traccar.password}")
    private String traccarPassword;

    private final RestTemplate restTemplate = new RestTemplate();

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

    // from/to must be ISO-8601 UTC strings, e.g. "2026-05-01T00:00:00.000Z"
    public List<?> getRoute(Integer deviceId, String from, String to) {
        URI uri = UriComponentsBuilder.fromHttpUrl(traccarUrl + "/api/reports/route")
                .queryParam("deviceId", deviceId)
                .queryParam("from", from)
                .queryParam("to", to)
                .build(true)
                .toUri();
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }
}
