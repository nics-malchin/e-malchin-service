package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.GpsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GpsPositionRepository extends JpaRepository<GpsPosition, Long> {

    boolean existsByImeiAndFixTime(String imei, LocalDateTime fixTime);

    List<GpsPosition> findByImeiAndFixTimeBetweenOrderByFixTimeAsc(
            String imei, LocalDateTime from, LocalDateTime to);

    Optional<GpsPosition> findTopByImeiOrderByFixTimeDesc(String imei);

    @Query("SELECT g FROM GpsPosition g WHERE g.fixTime = " +
           "(SELECT MAX(g2.fixTime) FROM GpsPosition g2 WHERE g2.imei = g.imei)")
    List<GpsPosition> findLatestPerImei();

    @Query("SELECT DISTINCT g.imei FROM GpsPosition g ORDER BY g.imei")
    List<String> findDistinctImeis();
}
