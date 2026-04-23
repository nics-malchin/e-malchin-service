package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "soil_survey_point", schema = "nics")
public class SoilSurveyPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "soil_type", length = 100)
    private String soilType;

    @Column(length = 50)
    private String depth;

    @Column(length = 1000)
    private String description;

    private LocalDate takenAt;

    private Double lat;
    private Double lng;
}
