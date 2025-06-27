//package com.nics.e_malchin_service.Service;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import java.util.Map;
//
//@Service
//public class TokenIntrospectionService {
//
//    @Value("${keycloak.introspect-url}")
//    private String introspectUrl;
//
//    @Value("${keycloak.client-id}")
//    private String clientId;
//
//    @Value("${keycloak.client-secret}")
//    private String clientSecret;
//
//    private RestTemplate restTemplate;
//
//    @PostConstruct
//    public void init() {
//        restTemplate = new RestTemplate();
//    }
//
//    public boolean isTokenValid(String token) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        headers.setBasicAuth(clientId, clientSecret); // basic auth = client_id + secret
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("token", token);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    introspectUrl,
//                    HttpMethod.POST,
//                    request,
//                    Map.class
//            );
//
//            return Boolean.TRUE.equals(response.getBody().get("active")); // токен хүчинтэй эсэх
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
