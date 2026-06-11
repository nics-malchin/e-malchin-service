package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.RoleManagementService;
import com.nics.e_malchin_service.dto.KeycloakUserDto;
import com.nics.e_malchin_service.dto.RoleAssignDto;
import com.nics.e_malchin_service.dto.RoleMenuConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/role-mgmt")
public class RoleManagementController {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementController.class);

    @Autowired
    private RoleManagementService svc;

    // ─── Menu config ─────────────────────────────────────────────────────────

    @GetMapping("/menu-config")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<RoleMenuConfigDto>> getAllMenuConfigs() {
        return ResponseEntity.ok(svc.getAllConfigs());
    }

    @GetMapping("/menu-config/my")
    public ResponseEntity<Set<String>> getMyMenuKeys(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = extractRoles(jwt);
        return ResponseEntity.ok(svc.getMenuKeysForRoles(roles));
    }

    @PostMapping("/menu-config")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> saveMenuConfig(@RequestBody RoleMenuConfigDto dto) {
        svc.saveConfig(dto);
        return ResponseEntity.ok().build();
    }

    // ─── Realm role CRUD ─────────────────────────────────────────────────────

    /** Lists all non-system realm roles from Keycloak. */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<String>> getRoles() {
        try {
            return ResponseEntity.ok(svc.getRealmRoles());
        } catch (Exception e) {
            return ResponseEntity.ok(RoleManagementService.KNOWN_ROLES);
        }
    }

    /** Creates a new realm role in Keycloak. */
    @PostMapping("/roles")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createRole(@RequestBody Map<String, String> body) {
        try {
            svc.createRealmRole(body.get("name"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Deletes a realm role from Keycloak and cleans up menu config. */
    @DeleteMapping("/roles/{roleName}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteRole(@PathVariable String roleName) {
        try {
            svc.deleteRealmRole(roleName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── User management ─────────────────────────────────────────────────────

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<KeycloakUserDto>> listUsers() {
        try {
            return ResponseEntity.ok(svc.listUsers());
        } catch (Exception e) {
            log.error("listUsers failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/users/assign-role")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> assignRole(@RequestBody RoleAssignDto dto) {
        try {
            svc.assignRole(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        if (jwt == null) return List.of();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return List.of();
        Object roles = realmAccess.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream()
                    .filter(r -> r instanceof String)
                    .map(r -> (String) r)
                    .toList();
        }
        return List.of();
    }
}
