package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "photo_monitoring_point", schema = "nics")
public class PhotoMonitoringPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String location;

    @Column(length = 1000)
    private String description;

    private LocalDate takenAt;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_name", length = 300)
    private String fileName;
}
