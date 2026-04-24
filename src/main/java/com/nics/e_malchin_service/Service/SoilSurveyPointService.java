package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.SoilSurveyPoint;
import com.nics.e_malchin_service.repository.SoilSurveyPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        existing.setPhH2o(updated.getPhH2o());
        existing.setCaco3Pct(updated.getCaco3Pct());
        existing.setSomPct(updated.getSomPct());
        existing.setSocPct(updated.getSocPct());
        existing.setEcDsM(updated.getEcDsM());
        existing.setP2o5Mg(updated.getP2o5Mg());
        existing.setK2oMg(updated.getK2oMg());
        existing.setCoverPct(updated.getCoverPct());
        existing.setBareAreaPct(updated.getBareAreaPct());
        existing.setAltitude(updated.getAltitude());
        existing.setBulkDensity(updated.getBulkDensity());
        existing.setSocStorageTc(updated.getSocStorageTc());
        existing.setTalhagdal(updated.getTalhagdal());
        existing.setZoneNum(updated.getZoneNum());
        existing.setBatchCode(updated.getBatchCode());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Map<String, Object> toGeoJson() {
        List<SoilSurveyPoint> points = repo.findAll().stream()
                .filter(p -> p.getLat() != null && p.getLng() != null)
                .collect(Collectors.toList());

        List<Map<String, Object>> features = new ArrayList<>();
        for (SoilSurveyPoint p : points) {
            Map<String, Object> geometry = new LinkedHashMap<>();
            geometry.put("type", "Point");
            geometry.put("coordinates", Arrays.asList(p.getLng(), p.getLat()));

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("id", p.getId());
            props.put("name", p.getName());
            props.put("depth", p.getDepth());
            props.put("soilType", p.getSoilType());
            props.put("talhagdal", p.getTalhagdal());
            props.put("phH2o", p.getPhH2o());
            props.put("somPct", p.getSomPct());
            props.put("socPct", p.getSocPct());
            props.put("p2o5Mg", p.getP2o5Mg());
            props.put("k2oMg", p.getK2oMg());
            props.put("coverPct", p.getCoverPct());
            props.put("altitude", p.getAltitude());
            props.put("zoneNum", p.getZoneNum());
            props.put("batchCode", p.getBatchCode());
            props.put("takenAt", p.getTakenAt() != null ? p.getTakenAt().toString() : null);

            Map<String, Object> feature = new LinkedHashMap<>();
            feature.put("type", "Feature");
            feature.put("properties", props);
            feature.put("geometry", geometry);
            features.add(feature);
        }

        Map<String, Object> fc = new LinkedHashMap<>();
        fc.put("type", "FeatureCollection");
        fc.put("features", features);
        return fc;
    }
}
