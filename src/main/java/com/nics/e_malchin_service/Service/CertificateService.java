package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Certificate;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.dto.CertificateDto;
import com.nics.e_malchin_service.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repo;
    private final LivestockDAO livestockDAO;
    private final UserDAO userDAO;

    public List<CertificateDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public List<CertificateDto> findByLivestock(Integer livestockId) {
        return repo.findByLivestockId(livestockId).stream().map(this::toDto).toList();
    }

    public CertificateDto create(CertificateDto dto) {
        Certificate e = new Certificate();
        fillEntity(e, dto);
        e.setCreatedBy(dto.getIssuedToId() != null ? dto.getIssuedToId() : 1000);
        return toDto(repo.save(e));
    }

    public CertificateDto update(CertificateDto dto) {
        Certificate e = repo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Certificate not found: " + dto.getId()));
        fillEntity(e, dto);
        return toDto(repo.save(e));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private void fillEntity(Certificate e, CertificateDto dto) {
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Livestock not found"));
            e.setLivestock(l);
        }
        if (dto.getIssuedToId() != null) {
            userDAO.findById(dto.getIssuedToId()).ifPresent(e::setIssuedTo);
        }
        e.setCertNumber(dto.getCertNumber());
        e.setCertType(dto.getCertType());
        e.setIssuedDate(dto.getIssuedDate());
        e.setExpiryDate(dto.getExpiryDate());
        e.setCertStatus(dto.getCertStatus());
        e.setNotes(dto.getNotes());
    }

    private CertificateDto toDto(Certificate e) {
        CertificateDto dto = new CertificateDto();
        dto.setId(e.getId());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        if (e.getIssuedTo() != null) {
            dto.setIssuedToId(e.getIssuedTo().getId());
            dto.setIssuedToName(e.getIssuedTo().getFirstName() + " " + e.getIssuedTo().getLastName());
        }
        dto.setCertNumber(e.getCertNumber());
        dto.setCertType(e.getCertType());
        dto.setIssuedDate(e.getIssuedDate());
        dto.setExpiryDate(e.getExpiryDate());
        dto.setCertStatus(e.getCertStatus());
        dto.setNotes(e.getNotes());
        return dto;
    }
}
