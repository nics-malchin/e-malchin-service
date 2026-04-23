package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.SoilSurveyPoint;
import com.nics.e_malchin_service.repository.SoilSurveyPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SoilSurveyPointService {

    private final SoilSurveyPointRepository repo;

    public List<SoilSurveyPoint> findAll() {
        return repo.findAll();
    }

    public SoilSurveyPoint create(SoilSurveyPoint p) {
        p.setCreatedBy(1000);
        return repo.save(p);
    }

    public SoilSurveyPoint update(SoilSurveyPoint updated) {
        SoilSurveyPoint existing = repo.findById(updated.getId())
                .orElseThrow(() -> new RuntimeException("SoilSurveyPoint not found: " + updated.getId()));
        existing.setName(updated.getName());
        existing.setSoilType(updated.getSoilType());
        existing.setDepth(updated.getDepth());
        existing.setDescription(updated.getDescription());
        existing.setTakenAt(updated.getTakenAt());
        existing.setLat(updated.getLat());
        existing.setLng(updated.getLng());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
