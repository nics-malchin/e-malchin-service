package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.PhotoMonitoringPoint;
import com.nics.e_malchin_service.repository.PhotoMonitoringPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoMonitoringService {

    private final PhotoMonitoringPointRepository repo;

    @Value("${nics.upload.path:uploads}")
    private String uploadPath;

    public List<PhotoMonitoringPoint> findAll() {
        return repo.findAll();
    }

    public PhotoMonitoringPoint upload(MultipartFile file, String title, String description,
                                       String category, String location, String takenAt) throws IOException {
        Path dir = Paths.get(uploadPath, "photos");
        Files.createDirectories(dir);

        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String stored = UUID.randomUUID() + ext;
        Path target = dir.resolve(stored);
        Files.copy(file.getInputStream(), target);

        PhotoMonitoringPoint p = new PhotoMonitoringPoint();
        p.setTitle(title);
        p.setDescription(description);
        p.setCategory(category);
        p.setLocation(location);
        p.setTakenAt(takenAt != null && !takenAt.isBlank() ? LocalDate.parse(takenAt) : null);
        p.setFilePath(target.toString());
        p.setFileName(stored);
        p.setCreatedBy(1000);
        return repo.save(p);
    }

    public Resource loadFile(Integer id) throws MalformedURLException {
        PhotoMonitoringPoint p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found: " + id));
        Path file = Paths.get(p.getFilePath());
        return new UrlResource(file.toUri());
    }

    public void delete(Integer id) {
        repo.findById(id).ifPresent(p -> {
            try {
                Path file = Paths.get(p.getFilePath());
                Files.deleteIfExists(file);
            } catch (IOException ignored) {}
            repo.delete(p);
        });
    }
}
