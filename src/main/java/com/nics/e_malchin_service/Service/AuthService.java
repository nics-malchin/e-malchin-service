package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.BahDAO;
import com.nics.e_malchin_service.DAO.HorshooDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Bah;
import com.nics.e_malchin_service.Entity.Horshoo;
import com.nics.e_malchin_service.Entity.User;
import com.nics.e_malchin_service.dto.LoginRequest;
import com.nics.e_malchin_service.dto.RegistrationRequest;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.ParseException;
import java.util.*;

@Service
public class AuthService {

    private final BahService bahService;
    private final HorshooService horshooService;
    private final UserService userService;
    private final BahDAO bahDAO;
    private final HorshooDAO horshooDAO;
    private final UserDAO userDAO;

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(BahService bahService,
                       HorshooService horshooService,
                       UserService userService,
                       BahDAO bahDAO,
                       HorshooDAO horshooDAO,
                       UserDAO userDAO) {
        this.bahService = bahService;
        this.horshooService = horshooService;
        this.userService = userService;
        this.bahDAO = bahDAO;
        this.horshooDAO = horshooDAO;
        this.userDAO = userDAO;
    }

    public Map<String, Object> login(LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.username());
        body.add("password", request.password());

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers), Map.class);
        Map<String, Object> responseBody = new HashMap<>(Objects.requireNonNull(response.getBody()));
        String accessToken = (String) responseBody.get("access_token");
        responseBody.put("roles", extractRoles(accessToken));
        return responseBody;
    }

    @Transactional
    public Map<String, Object> register(RegistrationRequest request) {
        // Validate
        validateRegistrationRequest(request);
        ensureUsernameAvailable(request.getUsername());

        // Admin token авах
        String adminToken = obtainServiceAccountToken();
        String keycloakUserId = null;

        try {
            // Keycloak-д хэрэглэгч үүсгэх
            keycloakUserId = createKeycloakUser(request, adminToken);

            // Role оноох
            assignRoleToUser(keycloakUserId, request.getRole(), adminToken);

            // Өөрийн домэйн (DB) дотор хадгалах
            Map<String, Object> persisted = persistDomainUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("keycloakUserId", keycloakUserId);
            response.put("role", request.getRole());
            response.putAll(persisted);
            return response;

        } catch (RuntimeException e) {
            // Keycloak дээр үүсгэчихээд DB дээр алдаа гарвал цэвэрлэх
            if (keycloakUserId != null) {
                try {
                    deleteKeycloakUser(keycloakUserId, adminToken);
                } catch (Exception ignored) {}
            }
            throw e;
        }
    }

    public void validateRegistrationRequest(RegistrationRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        if (!StringUtils.hasText(request.getRole())) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    public void ensureUsernameAvailable(String username) {
        boolean exists = userDAO.findByUsername(username).isPresent()
                || bahDAO.findByUsername(username).isPresent()
                || horshooDAO.findByUsername(username).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    public Map<String, Object> persistDomainUser(RegistrationRequest request) {
        Map<String, Object> response = new HashMap<>();
        String normalizedRole = request.getRole().trim().toLowerCase(Locale.ROOT);
        switch (normalizedRole) {
            case "malchin" -> {
                User user = new User();
                user.setUsername(request.getUsername());
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPin(request.getPin());
                user.setBah_id(request.getBahId());
                user.setHorshoo_id(request.getHorshooId());
                user.setIs_license_approved(request.getIsLicenseApproved());
                user.setPhone_number(request.getPhoneNumber());
                user.setFamily_id(request.getFamilyId());
                user.setAimag_id(request.getAimagId());
                user.setSum_id(request.getSumId());
                user.setBag_id(request.getBagId());
                user.setLocation_description(request.getLocationDescription());
                user.setHerder_count(request.getHerderCount());
                user.setFamily_count(request.getFamilyCount());
                User saved = userService.create(user);
                response.put("entity", "USER");
                response.put("entityId", saved.getId());
            }
            case "bah" -> {
                Bah bah = new Bah();
                bah.setName(request.getOrganizationName());
                bah.setUsername(request.getUsername());
                bah.setPassword(request.getPassword());
                bah.setHorshoo_id(request.getHorshooId());
                Bah saved = bahService.create(bah);
                response.put("entity", "BAH");
                response.put("entityId", saved.getId());
            }
            case "horshoo" -> {
                Horshoo horshoo = new Horshoo();
                horshoo.setName(request.getOrganizationName());
                horshoo.setUsername(request.getUsername());
                horshoo.setPassword(request.getPassword());
                Horshoo saved = horshooService.create(horshoo);
                response.put("entity", "HORSHOO");
                response.put("entityId", saved.getId());
            }
            default -> throw new IllegalArgumentException("Unsupported role: " + request.getRole());
        }
        return response;
    }

    private String obtainServiceAccountToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> responseBody = Objects.requireNonNull(response.getBody());
        return (String) responseBody.get("access_token");
    }



    public String createKeycloakUser(RegistrationRequest request, String adminToken) {
        HttpHeaders headers = createAuthHeaders(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("username", request.getUsername());
        payload.put("enabled", true);
        if (StringUtils.hasText(request.getEmail())) {
            payload.put("email", request.getEmail());
        }
        if (StringUtils.hasText(request.getFirstName())) {
            payload.put("firstName", request.getFirstName());
        }
        if (StringUtils.hasText(request.getLastName())) {
            payload.put("lastName", request.getLastName());
        }
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.getPassword(),
                "temporary", false
        )));

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(getAdminBaseUrl() + "/users", new HttpEntity<>(payload, headers), Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                URI location = response.getHeaders().getLocation();
                if (location != null) {
                    String path = location.getPath();
                    return path.substring(path.lastIndexOf('/') + 1);
                }
            }
        } catch (HttpClientErrorException.Conflict conflict) {
            throw new IllegalArgumentException("Keycloak user already exists");
        }

        return fetchUserIdByUsername(request.getUsername(), adminToken);
    }

    public void assignRoleToUser(String userId, String role, String adminToken) {
        HttpHeaders headers = createAuthHeaders(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, Object>> roleResponse = restTemplate.exchange(
                getAdminBaseUrl() + "/roles/" + role,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> roleRepresentation = roleResponse.getBody();
        if (roleRepresentation == null) {
            throw new IllegalArgumentException("Unable to resolve Keycloak role: " + role);
        }

        restTemplate.exchange(
                getAdminBaseUrl() + "/users/" + userId + "/role-mappings/realm",
                HttpMethod.POST,
                new HttpEntity<>(List.of(roleRepresentation), headers),
                Void.class
        );
    }

    private String fetchUserIdByUsername(String username, String adminToken) {
        ResponseEntity<List<Map<String, Object>>> searchResponse = restTemplate.exchange(
                getAdminBaseUrl() + "/users?username=" + username,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders(adminToken)),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return searchResponse.getBody()
                .stream()
                .filter(user -> username.equalsIgnoreCase(String.valueOf(user.get("username"))))
                .map(user -> String.valueOf(user.get("id")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve Keycloak user id for " + username));
    }

    public void deleteKeycloakUser(String userId, String adminToken) {
        restTemplate.exchange(
                getAdminBaseUrl() + "/users/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(createAuthHeaders(adminToken)),
                Void.class
        );
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private String getAdminBaseUrl() {
        return "https://nics-malchin.mn/admin/realms/nics";
    }

    public List<String> extractRoles(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            return Collections.emptyList();
        }
        try {
            JWTClaimsSet claims = JWTParser.parse(accessToken).getJWTClaimsSet();
            Object realmAccess = claims.getClaim("realm_access");
            if (realmAccess instanceof Map<?, ?> map) {
                Object roles = map.get("roles");
                if (roles instanceof List<?> list) {
                    List<String> result = new ArrayList<>();
                    for (Object role : list) {
                        result.add(String.valueOf(role));
                    }
                    return result;
                }
            }
        } catch (ParseException e) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}

