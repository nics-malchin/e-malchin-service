package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tracker_device")
public class TrackerDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String imei;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "device_model", length = 50)
    private String deviceModel;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    void prePersist() {
        if (registeredAt == null) registeredAt = LocalDateTime.now();
    }
}
