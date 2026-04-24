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

    @Column(name = "zone_num")
    private Integer zoneNum;

    @Column(name = "bah_name", length = 200)
    private String bahName;

    @Column(name = "area_ha")
    private Double areaHa;

    // "talbai" | "bah"
    @Column(name = "zone_type", length = 50)
    private String zoneType;
}
