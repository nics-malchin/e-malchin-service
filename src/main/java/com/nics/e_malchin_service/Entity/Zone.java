package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "zone", schema = "nics")
public class Zone extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String coordinates; // JSON: [[lat,lng], [lat,lng], ...]

    private String color;

    private String category;
}
