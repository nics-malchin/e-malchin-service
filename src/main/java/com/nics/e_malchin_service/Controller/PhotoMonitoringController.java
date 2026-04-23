package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.PhotoMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/photo-monitoring")
@RequiredArgsConstructor
public class PhotoMonitoringController {

    private final PhotoMonitoringService service;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            @RequestParam(value = "category", required = false, defaultValue = "Бусад") String category,
            @RequestParam(value = "location", required = false, defaultValue = "") String location,
            @RequestParam(value = "takenAt", required = false, defaultValue = "") String takenAt
    ) throws IOException {
        return ResponseEntity.ok(service.upload(file, title, description, category, location, takenAt));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<Resource> serveFile(@PathVariable Integer id) throws IOException {
        Resource resource = service.loadFile(id);
        String contentType = "image/jpeg";
        try {
            String filename = resource.getFilename();
            if (filename != null) {
                if (filename.endsWith(".png")) contentType = "image/png";
                else if (filename.endsWith(".gif")) contentType = "image/gif";
                else if (filename.endsWith(".webp")) contentType = "image/webp";
            }
        } catch (Exception ignored) {}
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
