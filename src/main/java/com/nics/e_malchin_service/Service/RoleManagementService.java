package com.nics.e_malchin_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nics.e_malchin_service.Entity.RoleMenuConfig;
import com.nics.e_malchin_service.dto.KeycloakUserDto;
import com.nics.e_malchin_service.dto.RoleAssignDto;
import com.nics.e_malchin_service.dto.RoleMenuConfigDto;
import com.nics.e_malchin_service.repository.RoleMenuConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleManagementService {

    // ─── Default menu-key → role mapping ────────────────────────────────────
    private static final Map<String, List<String>> DEFAULT_ROLE_MENUS = Map.of(
        "admin",   List.of("dashboard","livestock","malchin","bah","horshoo","create-user",
                           "survey","zone","animal-health","lab-result","certificate",
                           "traceability","gis-dashboard","gps-device","photo-monitoring",
                           "soil-survey","my-documents","role-management"),
        "bah",     List.of("dashboard","livestock","malchin","create-user","animal-health",
                           "lab-result","certificate","traceability","gis-dashboard",
                           "gps-device","photo-monitoring","soil-survey"),
        "horshoo", List.of("dashboard","livestock","malchin","bah","create-user"),
        "malchin", List.of("dashboard","livestock"),
        "vet",     List.of("dashboard","animal-health","lab-result","certificate","traceability"),
        "factory", List.of("dashboard")
    );

    // Fallback list used when Keycloak is unreachable
    public static final List<String> KNOWN_ROLES =
            List.of("admin","bah","horshoo","malchin","vet","factory");

    // Roles that must never be deleted through this API
    private static final Set<String> PROTECTED_ROLES = Set.of("admin");

    // Keycloak internal system roles to exclude from the app role list
    private static final Set<String> SYSTEM_ROLES_EXCLUDE = Set.of("offline_access", "uma_authorization");

    @Autowired
    private RoleMenuConfigRepository repo;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUser;

    @Value("${keycloak.admin.password}")
    private String adminPass;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // ─── Realm role CRUD ─────────────────────────────────────────────────────

    /** Returns all non-system realm roles sorted alphabetically. */
    @SuppressWarnings("unchecked")
    public List<String> getRealmRoles() {
        String token = adminToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/roles?max=200";
        ResponseEntity<List> resp = rest.exchange(url, HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(token)), List.class);
        List<Map<String, Object>> roles = resp.getBody() != null ? resp.getBody() : List.of();
        return roles.stream()
                .map(r -> (String) r.get("name"))
                .filter(n -> n != null
                        && !n.startsWith("default-roles-")
                        && !SYSTEM_ROLES_EXCLUDE.contains(n))
                .sorted()
                .collect(Collectors.toList());
    }

    /** Creates a new realm role in Keycloak. */
    public void createRealmRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        String name = roleName.trim().toLowerCase().replaceAll("[^a-z0-9_-]", "-");
        String token = adminToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/roles";
        rest.postForEntity(url,
                new HttpEntity<>(Map.of("name", name, "description", ""), bearerHeaders(token)),
                String.class);
    }

    /** Deletes a realm role from Keycloak and removes its menu config from DB. */
    @Transactional
    public void deleteRealmRole(String roleName) {
        if (PROTECTED_ROLES.contains(roleName)) {
            throw new IllegalArgumentException("Cannot delete protected role: " + roleName);
        }
        String token = adminToken();
        String url = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        rest.exchange(url, HttpMethod.DELETE, new HttpEntity<>(bearerHeaders(token)), String.class);
        repo.deleteByRoleName(roleName);
    }

    // ─── Menu config ─────────────────────────────────────────────────────────

    /** Returns config for ALL realm roles (DB config if saved, default otherwise). */
    public List<RoleMenuConfigDto> getAllConfigs() {
        List<String> roles;
        try {
            roles = getRealmRoles();
        } catch (Exception e) {
            roles = KNOWN_ROLES;
        }
        return roles.stream()
                .map(role -> {
                    List<RoleMenuConfig> rows = repo.findByRoleName(role);
                    RoleMenuConfigDto dto = new RoleMenuConfigDto();
                    dto.setRoleName(role);
                    dto.setMenuKeys(rows.isEmpty()
                            ? DEFAULT_ROLE_MENUS.getOrDefault(role, List.of())
                            : rows.stream().map(RoleMenuConfig::getMenuKey).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /** Returns union of allowed menu keys for the given user roles. */
    public Set<String> getMenuKeysForRoles(List<String> userRoles) {
        Set<String> result = new HashSet<>();
        for (String role : userRoles) {
            List<RoleMenuConfig> rows = repo.findByRoleName(role);
            if (rows.isEmpty()) {
                result.addAll(DEFAULT_ROLE_MENUS.getOrDefault(role, List.of()));
            } else {
                rows.forEach(r -> result.add(r.getMenuKey()));
            }
        }
        return result;
    }

    /** Replaces the full menu-key list for a role atomically. */
    @Transactional
    public void saveConfig(RoleMenuConfigDto dto) {
        repo.deleteByRoleName(dto.getRoleName());
        if (dto.getMenuKeys() != null) {
            for (String key : dto.getMenuKeys()) {
                RoleMenuConfig row = new RoleMenuConfig();
                row.setRoleName(dto.getRoleName());
                row.setMenuKey(key);
                repo.save(row);
            }
        }
    }

    // ─── Keycloak user + role management ─────────────────────────────────────

    /** Lists all users in the Keycloak realm, enriched with their realm roles. */
    @SuppressWarnings("unchecked")
    public List<KeycloakUserDto> listUsers() {
        String token = adminToken();
        HttpHeaders hdr = bearerHeaders(token);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users?max=200";
        ResponseEntity<List> resp = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(hdr), List.class);
        List<Map<String, Object>> raw = resp.getBody() != null ? resp.getBody() : List.of();

        return raw.stream().map(u -> {
            KeycloakUserDto dto = new KeycloakUserDto();
            dto.setId((String) u.get("id"));
            dto.setUsername((String) u.get("username"));
            dto.setFirstName((String) u.getOrDefault("firstName", ""));
            dto.setLastName((String) u.getOrDefault("lastName", ""));
            dto.setEmail((String) u.getOrDefault("email", ""));
            dto.setRoles(fetchUserRealmRoles(dto.getId(), token));
            return dto;
        }).collect(Collectors.toList());
    }

    /** Assigns a realm role to a Keycloak user, removing all previous realm roles first. */
    public void assignRole(RoleAssignDto dto) {
        String token = adminToken();

        List<String> current = fetchUserRealmRoles(dto.getUserId(), token);
        for (String roleName : current) {
            removeRole(dto.getUserId(), roleName, token);
        }

        if (dto.getRoleName() != null && !dto.getRoleName().isBlank()) {
            addRole(dto.getUserId(), dto.getRoleName(), token);
        }
    }

    // ─── Internal helpers ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<String> fetchUserRealmRoles(String userId, String token) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        try {
            ResponseEntity<List> resp = rest.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(bearerHeaders(token)), List.class);
            List<Map<String, Object>> roles = resp.getBody() != null ? resp.getBody() : List.of();
            return roles.stream()
                    .map(r -> (String) r.get("name"))
                    .filter(n -> n != null
                            && !n.startsWith("default-roles-")
                            && !SYSTEM_ROLES_EXCLUDE.contains(n))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private void addRole(String userId, String roleName, String token) {
        String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        ResponseEntity<Map> roleResp = rest.exchange(roleUrl, HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(token)), Map.class);
        Map<String, Object> role = roleResp.getBody();

        String assignUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        rest.postForEntity(assignUrl, new HttpEntity<>(List.of(role), bearerHeaders(token)), String.class);
    }

    @SuppressWarnings("unchecked")
    private void removeRole(String userId, String roleName, String token) {
        try {
            String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
            ResponseEntity<Map> roleResp = rest.exchange(roleUrl, HttpMethod.GET,
                    new HttpEntity<>(bearerHeaders(token)), Map.class);
            Map<String, Object> role = roleResp.getBody();

            String removeUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
            rest.exchange(removeUrl, HttpMethod.DELETE,
                    new HttpEntity<>(List.of(role), bearerHeaders(token)), String.class);
        } catch (Exception ignored) {}
    }

    private String adminToken() {
        HttpHeaders hdr = new HttpHeaders();
        hdr.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli");
        params.add("username", adminUser);
        params.add("password", adminPass);

        ResponseEntity<Map> resp = rest.postForEntity(
                keycloakUrl + "/realms/master/protocol/openid-connect/token",
                new HttpEntity<>(params, hdr), Map.class);

        if (resp.getBody() == null || resp.getBody().get("access_token") == null) {
            throw new RuntimeException("Keycloak admin token авахад алдаа гарлаа.");
        }
        return (String) resp.getBody().get("access_token");
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders hdr = new HttpHeaders();
        hdr.setBearerAuth(token);
        hdr.setContentType(MediaType.APPLICATION_JSON);
        return hdr;
    }
}
