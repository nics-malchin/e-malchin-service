package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
    name = "gps_position",
    uniqueConstraints = @UniqueConstraint(name = "uq_gps_position_imei_fixtime", columnNames = {"imei", "fix_time"})
)
public class GpsPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String imei;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double speed;
    private Double course;
    private Double altitude;

    @Column(name = "fix_time", nullable = false)
    private LocalDateTime fixTime;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    private Integer sats;

    @Column(name = "battery_volt")
    private Double batteryVolt;

    @Column(name = "signal_level")
    private Integer signalLevel;
}
