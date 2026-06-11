package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.HorshooDocument;
import com.nics.e_malchin_service.repository.HorshooDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class HorshooDocumentService {

    @Value("${nics.upload.path:uploads}")
    private String uploadRoot;

    private final HorshooDocumentRepository repo;

    public HorshooDocumentService(HorshooDocumentRepository repo) {
        this.repo = repo;
    }

    public HorshooDocument upload(Integer horshooId, MultipartFile file, String description) throws IOException {
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String stored = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadRoot, "horshoo", String.valueOf(horshooId));
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(stored), StandardCopyOption.REPLACE_EXISTING);

        HorshooDocument doc = new HorshooDocument();
        doc.setHorshooId(horshooId);
        doc.setOriginalName(original);
        doc.setStoredName(stored);
        doc.setMimeType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setDescription(description);
        return repo.save(doc);
    }

    public List<HorshooDocument> listByHorshoo(Integer horshooId) {
        return repo.findByHorshooIdAndActiveFlagTrueOrderByCreatedDateDesc(horshooId);
    }

    public ResponseEntity<Resource> download(Integer docId) throws MalformedURLException {
        HorshooDocument doc = repo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));

        Path file = Paths.get(uploadRoot, "horshoo", String.valueOf(doc.getHorshooId()), doc.getStoredName());
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        String mime = doc.getMimeType() != null ? doc.getMimeType() : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getOriginalName() + "\"")
                .body(resource);
    }

    public void delete(Integer docId) {
        HorshooDocument doc = repo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
        doc.setActiveFlag(false);
        repo.save(doc);
    }
}
