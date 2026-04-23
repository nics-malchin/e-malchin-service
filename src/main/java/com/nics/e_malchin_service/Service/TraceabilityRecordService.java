package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.TraceabilityRecord;
import com.nics.e_malchin_service.dto.TraceabilityRecordDto;
import com.nics.e_malchin_service.repository.TraceabilityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TraceabilityRecordService {

    private final TraceabilityRecordRepository repo;
    private final LivestockDAO livestockDAO;
    private final UserDAO userDAO;

    public List<TraceabilityRecordDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public List<TraceabilityRecordDto> findByLivestock(Integer livestockId) {
        return repo.findByLivestockId(livestockId).stream().map(this::toDto).toList();
    }

    public TraceabilityRecordDto create(TraceabilityRecordDto dto) {
        TraceabilityRecord e = new TraceabilityRecord();
        fillEntity(e, dto);
        e.setCreatedBy(dto.getRecordedById() != null ? dto.getRecordedById() : 1000);
        return toDto(repo.save(e));
    }

    public TraceabilityRecordDto update(TraceabilityRecordDto dto) {
        TraceabilityRecord e = repo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("TraceabilityRecord not found: " + dto.getId()));
        fillEntity(e, dto);
        return toDto(repo.save(e));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private void fillEntity(TraceabilityRecord e, TraceabilityRecordDto dto) {
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Livestock not found"));
            e.setLivestock(l);
        }
        if (dto.getRecordedById() != null) {
            userDAO.findById(dto.getRecordedById()).ifPresent(e::setRecordedBy);
        }
        e.setStepType(dto.getStepType());
        e.setDescription(dto.getDescription());
        e.setEvidenceRef(dto.getEvidenceRef());
        e.setRecordedAt(dto.getRecordedAt());
    }

    private TraceabilityRecordDto toDto(TraceabilityRecord e) {
        TraceabilityRecordDto dto = new TraceabilityRecordDto();
        dto.setId(e.getId());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        if (e.getRecordedBy() != null) {
            dto.setRecordedById(e.getRecordedBy().getId());
            dto.setRecordedByName(e.getRecordedBy().getFirstName() + " " + e.getRecordedBy().getLastName());
        }
        dto.setStepType(e.getStepType());
        dto.setDescription(e.getDescription());
        dto.setEvidenceRef(e.getEvidenceRef());
        dto.setRecordedAt(e.getRecordedAt());
        return dto;
    }
}
