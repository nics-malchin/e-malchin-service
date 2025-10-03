package com.nics.e_malchin_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", loginRequest.get("username"));
        body.add("password", loginRequest.get("password"));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token response empty");
            }

            String accessToken = (String) tokenResponse.get("access_token");
            List<String> roles = extractRolesFromToken(accessToken);

            Optional<String> role = roles.stream()
                    .filter(r -> "bah".equals(r) || "malchin".equals(r) || "horshoo".equals(r) || "admin".equals(r))
                    .findFirst();

            role.ifPresent(s -> tokenResponse.put("role", s));

            tokenResponse.put("roles", roles);

            return ResponseEntity.ok(tokenResponse);

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private List<String> extractRolesFromToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                return (List<String>) realmAccess.get("roles");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
