package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.AnimalHealth;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.dto.AnimalHealthDto;
import com.nics.e_malchin_service.repository.AnimalHealthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalHealthService {

    private final AnimalHealthRepository repo;
    private final LivestockDAO livestockDAO;
    private final UserDAO userDAO;

    public List<AnimalHealthDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public List<AnimalHealthDto> findByLivestock(Integer livestockId) {
        return repo.findByLivestockId(livestockId).stream().map(this::toDto).toList();
    }

    public AnimalHealthDto create(AnimalHealthDto dto) {
        AnimalHealth e = new AnimalHealth();
        fillEntity(e, dto);
        e.setCreatedBy(dto.getDiagnosedById() != null ? dto.getDiagnosedById() : 1000);
        return toDto(repo.save(e));
    }

    public AnimalHealthDto update(AnimalHealthDto dto) {
        AnimalHealth e = repo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("AnimalHealth not found: " + dto.getId()));
        fillEntity(e, dto);
        return toDto(repo.save(e));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private void fillEntity(AnimalHealth e, AnimalHealthDto dto) {
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Livestock not found: " + dto.getLivestockId()));
            e.setLivestock(l);
        }
        if (dto.getDiagnosedById() != null) {
            userDAO.findById(dto.getDiagnosedById()).ifPresent(e::setDiagnosedBy);
        }
        e.setExaminationDate(dto.getExaminationDate());
        e.setSymptoms(dto.getSymptoms());
        e.setDiagnosis(dto.getDiagnosis());
        e.setTreatment(dto.getTreatment());
        e.setNextCheckDate(dto.getNextCheckDate());
        e.setNotes(dto.getNotes());
    }

    private AnimalHealthDto toDto(AnimalHealth e) {
        AnimalHealthDto dto = new AnimalHealthDto();
        dto.setId(e.getId());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        if (e.getDiagnosedBy() != null) {
            dto.setDiagnosedById(e.getDiagnosedBy().getId());
            dto.setDiagnosedByName(e.getDiagnosedBy().getFirstName() + " " + e.getDiagnosedBy().getLastName());
        }
        dto.setExaminationDate(e.getExaminationDate());
        dto.setSymptoms(e.getSymptoms());
        dto.setDiagnosis(e.getDiagnosis());
        dto.setTreatment(e.getTreatment());
        dto.setNextCheckDate(e.getNextCheckDate());
        dto.setNotes(e.getNotes());
        return dto;
    }
}
