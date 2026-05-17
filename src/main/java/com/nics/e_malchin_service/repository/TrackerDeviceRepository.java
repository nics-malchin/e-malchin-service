package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.TrackerDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackerDeviceRepository extends JpaRepository<TrackerDevice, Integer> {
    Optional<TrackerDevice> findByImei(String imei);
    boolean existsByImei(String imei);
    List<TrackerDevice> findByActiveTrue();
}
