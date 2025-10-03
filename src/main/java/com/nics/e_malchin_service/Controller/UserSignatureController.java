package com.nics.e_malchin_service.Controller;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.User;
import com.nics.e_malchin_service.Entity.UserSignature;
import com.nics.e_malchin_service.Service.UserSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class UserSignatureController {

    private final UserSignatureService service;
    private final UserDAO userDAO;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSignature(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            UserSignature saved = service.saveSignature(userId, file);
            User user = userDAO.findById(userId);
            user.setIs_license_approved(1);
            userDAO.save(user);
            return ResponseEntity.ok(saved.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getSignature(@PathVariable Long id) {
        byte[] data = service.getSignature(id);
        if (data == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(data);
    }
}
