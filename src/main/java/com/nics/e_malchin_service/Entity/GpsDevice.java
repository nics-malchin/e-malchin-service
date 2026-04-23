package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gps_device", schema = "nics")
public class GpsDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    @Column(name = "device_code", length = 100)
    private String deviceCode;

    private Double latitude;
    private Double longitude;
    private Double altitude;
    private LocalDateTime recordedAt;
    private Integer batteryLevel;

    @Transient
    private Integer livestockId;
}
