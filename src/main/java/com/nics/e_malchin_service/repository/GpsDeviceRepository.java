package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.GpsDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GpsDeviceRepository extends JpaRepository<GpsDevice, Integer> {

    @Query("SELECT g FROM GpsDevice g WHERE g.livestock.id = :livestockId ORDER BY g.recordedAt DESC")
    List<GpsDevice> findByLivestockId(Integer livestockId);

    @Query("SELECT g FROM GpsDevice g WHERE g.deviceCode = :deviceCode ORDER BY g.recordedAt DESC")
    List<GpsDevice> findByDeviceCode(String deviceCode);
}
