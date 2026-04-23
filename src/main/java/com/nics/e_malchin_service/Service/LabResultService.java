package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.Entity.LabResult;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.dto.LabResultDto;
import com.nics.e_malchin_service.repository.LabResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabResultService {

    private final LabResultRepository repo;
    private final LivestockDAO livestockDAO;

    public List<LabResultDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public List<LabResultDto> findByLivestock(Integer livestockId) {
        return repo.findByLivestockId(livestockId).stream().map(this::toDto).toList();
    }

    public LabResultDto create(LabResultDto dto) {
        LabResult e = new LabResult();
        fillEntity(e, dto);
        e.setCreatedBy(1000);
        return toDto(repo.save(e));
    }

    public LabResultDto update(LabResultDto dto) {
        LabResult e = repo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("LabResult not found: " + dto.getId()));
        fillEntity(e, dto);
        return toDto(repo.save(e));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private void fillEntity(LabResult e, LabResultDto dto) {
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Livestock not found: " + dto.getLivestockId()));
            e.setLivestock(l);
        }
        e.setSampleDate(dto.getSampleDate());
        e.setResultDate(dto.getResultDate());
        e.setTestType(dto.getTestType());
        e.setLabName(dto.getLabName());
        e.setResult(dto.getResult());
        e.setCertified(dto.getCertified());
        e.setNotes(dto.getNotes());
    }

    private LabResultDto toDto(LabResult e) {
        LabResultDto dto = new LabResultDto();
        dto.setId(e.getId());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        dto.setSampleDate(e.getSampleDate());
        dto.setResultDate(e.getResultDate());
        dto.setTestType(e.getTestType());
        dto.setLabName(e.getLabName());
        dto.setResult(e.getResult());
        dto.setCertified(e.getCertified());
        dto.setNotes(e.getNotes());
        return dto;
    }
}
