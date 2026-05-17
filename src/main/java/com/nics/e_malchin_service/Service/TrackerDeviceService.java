package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.Entity.GpsPosition;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.TrackerDevice;
import com.nics.e_malchin_service.dto.TrackerDeviceDto;
import com.nics.e_malchin_service.repository.GpsPositionRepository;
import com.nics.e_malchin_service.repository.TrackerDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrackerDeviceService {

    private final TrackerDeviceRepository deviceRepo;
    private final GpsPositionRepository positionRepo;
    private final LivestockDAO livestockDAO;

    public List<TrackerDeviceDto> findAll() {
        List<TrackerDevice> devices = deviceRepo.findAll();

        // Latest position per IMEI from gps_position
        Map<String, GpsPosition> latestMap = new HashMap<>();
        positionRepo.findLatestPerImei().forEach(p -> latestMap.put(p.getImei(), p));

        // IMEIs that have data in DB but not yet registered as TrackerDevice
        Set<String> registeredImeis = new HashSet<>();
        devices.forEach(d -> registeredImeis.add(d.getImei()));

        List<TrackerDeviceDto> result = new ArrayList<>();
        for (TrackerDevice d : devices) {
            result.add(toDto(d, latestMap.get(d.getImei())));
        }

        // Auto-include unregistered IMEIs from gps_position (read-only, no id)
        for (Map.Entry<String, GpsPosition> e : latestMap.entrySet()) {
            if (!registeredImeis.contains(e.getKey())) {
                result.add(syntheticDto(e.getKey(), e.getValue()));
            }
        }

        return result;
    }

    public TrackerDeviceDto create(TrackerDeviceDto dto) {
        if (deviceRepo.existsByImei(dto.getImei())) {
            throw new IllegalArgumentException("IMEI бүртгэлтэй байна: " + dto.getImei());
        }
        TrackerDevice e = new TrackerDevice();
        fill(e, dto);
        return toDto(deviceRepo.save(e), null);
    }

    public TrackerDeviceDto update(Integer id, TrackerDeviceDto dto) {
        TrackerDevice e = deviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("TrackerDevice олдсонгүй: " + id));
        fill(e, dto);
        GpsPosition latest = positionRepo.findTopByImeiOrderByFixTimeDesc(e.getImei()).orElse(null);
        return toDto(deviceRepo.save(e), latest);
    }

    public void delete(Integer id) {
        deviceRepo.deleteById(id);
    }

    private void fill(TrackerDevice e, TrackerDeviceDto dto) {
        e.setImei(dto.getImei());
        e.setName(dto.getName());
        e.setDeviceModel(dto.getDeviceModel());
        e.setNotes(dto.getNotes());
        e.setActive(dto.getActive() != null ? dto.getActive() : true);
        if (dto.getLivestockId() != null) {
            Livestock l = livestockDAO.findById(dto.getLivestockId())
                    .orElseThrow(() -> new RuntimeException("Мал олдсонгүй: " + dto.getLivestockId()));
            e.setLivestock(l);
        } else {
            e.setLivestock(null);
        }
    }

    private TrackerDeviceDto toDto(TrackerDevice e, GpsPosition pos) {
        TrackerDeviceDto dto = new TrackerDeviceDto();
        dto.setId(e.getId());
        dto.setImei(e.getImei());
        dto.setUniqueId(e.getImei());
        dto.setName(e.getName());
        dto.setDeviceModel(e.getDeviceModel());
        dto.setNotes(e.getNotes());
        dto.setActive(e.getActive());
        dto.setRegisteredAt(e.getRegisteredAt());
        if (e.getLivestock() != null) {
            dto.setLivestockId(e.getLivestock().getId());
            dto.setLivestockCode(e.getLivestock().getCode());
        }
        enrichWithPosition(dto, pos);
        return dto;
    }

    private TrackerDeviceDto syntheticDto(String imei, GpsPosition pos) {
        TrackerDeviceDto dto = new TrackerDeviceDto();
        dto.setImei(imei);
        dto.setUniqueId(imei);
        dto.setName(imei);
        dto.setActive(true);
        enrichWithPosition(dto, pos);
        return dto;
    }

    private void enrichWithPosition(TrackerDeviceDto dto, GpsPosition pos) {
        if (pos == null) {
            dto.setStatus("unknown");
            return;
        }
        dto.setLastUpdate(pos.getFixTime());
        dto.setLastLatitude(pos.getLatitude());
        dto.setLastLongitude(pos.getLongitude());
        dto.setLastSpeed(pos.getSpeed());
        dto.setLastSats(pos.getSats());
        dto.setBatteryVolt(pos.getBatteryVolt());
        dto.setSignalLevel(pos.getSignalLevel());

        if (pos.getBatteryVolt() != null) {
            int pct = (int) Math.round(Math.min(100, Math.max(0,
                    (pos.getBatteryVolt() - 3.5) / 0.7 * 100)));
            dto.setBatteryPct(pct);
        }

        // Online: last fix within 10 minutes
        dto.setStatus(pos.getFixTime().isAfter(LocalDateTime.now().minusMinutes(10))
                ? "online" : "offline");
    }
}
