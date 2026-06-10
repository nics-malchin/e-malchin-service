package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.RoleManagementService;
import com.nics.e_malchin_service.dto.KeycloakUserDto;
import com.nics.e_malchin_service.dto.RoleAssignDto;
import com.nics.e_malchin_service.dto.RoleMenuConfigDto;
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

    @Autowired
    private RoleManagementService svc;

    // ─── Menu config ─────────────────────────────────────────────────────────

    /** Full config for every role — used by the admin settings UI. */
    @GetMapping("/menu-config")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<RoleMenuConfigDto>> getAllMenuConfigs() {
        return ResponseEntity.ok(svc.getAllConfigs());
    }

    /**
     * Returns the union of allowed menu keys for the currently authenticated user.
     * Called on every login to drive sidebar visibility.
     */
    @GetMapping("/menu-config/my")
    public ResponseEntity<Set<String>> getMyMenuKeys(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = extractRoles(jwt);
        return ResponseEntity.ok(svc.getMenuKeysForRoles(roles));
    }

    /** Save (replace) menu-key list for a single role. */
    @PostMapping("/menu-config")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> saveMenuConfig(@RequestBody RoleMenuConfigDto dto) {
        svc.saveConfig(dto);
        return ResponseEntity.ok().build();
    }

    // ─── Role list ────────────────────────────────────────────────────────────

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(RoleManagementService.KNOWN_ROLES);
    }

    // ─── User management ─────────────────────────────────────────────────────

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<KeycloakUserDto>> listUsers() {
        try {
            return ResponseEntity.ok(svc.listUsers());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Replaces all existing realm roles for the given user with the specified role. */
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
