package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.Entity.GpsDevice;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.dto.GpsDeviceDto;
import com.nics.e_malchin_service.repository.GpsDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsDeviceService {

    private final GpsDeviceRepository repo;
    private final LivestockDAO livestockDAO;

    public List<GpsDeviceDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public List<GpsDeviceDto> findByLivestock(Integer livestockId) {
        return repo.findByLivestockId(livestockId).stream().map(this::toDto).toList();
    }

    public GpsDeviceDto create(GpsDeviceDto dto) {
        GpsDevice e = new GpsDevice();
        fillEntity(e, dto);
        e.setCreatedBy(1000);
        return toDto(repo.save(e));
    }

    public GpsDeviceDto update(GpsDeviceDto dto) {
        GpsDevice e = repo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("GpsDevice not found: " + dto.getId()));
        fillEntity(e, dto);
        return toDto(repo.save(e));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private void fillEntity(GpsDevice e, GpsDeviceDto dto) {
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Livestock not found: " + dto.getLivestockId()));
            e.setLivestock(l);
        }
        e.setDeviceCode(dto.getDeviceCode());
        e.setLatitude(dto.getLatitude());
        e.setLongitude(dto.getLongitude());
        e.setAltitude(dto.getAltitude());
        e.setRecordedAt(dto.getRecordedAt());
        e.setBatteryLevel(dto.getBatteryLevel());
    }

    private GpsDeviceDto toDto(GpsDevice e) {
        GpsDeviceDto dto = new GpsDeviceDto();
        dto.setId(e.getId());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        dto.setDeviceCode(e.getDeviceCode());
        dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude());
        dto.setAltitude(e.getAltitude());
        dto.setRecordedAt(e.getRecordedAt());
        dto.setBatteryLevel(e.getBatteryLevel());
        return dto;
    }
}
