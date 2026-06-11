package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.HorshooDocument;
import com.nics.e_malchin_service.Entity.User;
import com.nics.e_malchin_service.Service.HorshooDocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/horshoo")
public class HorshooDocumentController {

    private final HorshooDocumentService service;
    private final UserDAO userDAO;

    public HorshooDocumentController(HorshooDocumentService service, UserDAO userDAO) {
        this.service = service;
        this.userDAO = userDAO;
    }

    // ── Admin endpoints (by horshoo id) ─────────────────────────────────────

    @PostMapping("/{id}/documents")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<HorshooDocument> upload(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) throws IOException {
        return ResponseEntity.ok(service.upload(id, file, description));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<HorshooDocument>> list(@PathVariable Integer id) {
        return ResponseEntity.ok(service.listByHorshoo(id));
    }

    @GetMapping("/documents/{docId}/file")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public ResponseEntity<Resource> download(@PathVariable Integer docId) throws MalformedURLException {
        return service.download(docId);
    }

    @DeleteMapping("/documents/{docId}")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public ResponseEntity<Void> delete(@PathVariable Integer docId) {
        service.delete(docId);
        return ResponseEntity.ok().build();
    }

    // ── Horshoo self-service endpoints (JWT → own horshoo) ──────────────────

    @GetMapping("/my/documents")
    @PreAuthorize("hasRole('horshoo')")
    public ResponseEntity<List<HorshooDocument>> myList(@AuthenticationPrincipal Jwt jwt) {
        Integer horshooId = resolveHorshooId(jwt);
        return ResponseEntity.ok(service.listByHorshoo(horshooId));
    }

    @PostMapping("/my/documents")
    @PreAuthorize("hasRole('horshoo')")
    public ResponseEntity<HorshooDocument> myUpload(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) throws IOException {
        Integer horshooId = resolveHorshooId(jwt);
        return ResponseEntity.ok(service.upload(horshooId, file, description));
    }

    private Integer resolveHorshooId(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (user.getHorshoo_id() == null) {
            throw new RuntimeException("User has no horshoo linked: " + username);
        }
        return user.getHorshoo_id();
    }
}
