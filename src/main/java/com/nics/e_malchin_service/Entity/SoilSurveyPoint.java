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

    // Chemistry fields from lab analysis
    @Column(name = "ph_h2o")
    private Double phH2o;

    @Column(name = "caco3_pct")
    private Double caco3Pct;

    @Column(name = "som_pct")
    private Double somPct;

    @Column(name = "soc_pct")
    private Double socPct;

    @Column(name = "ec_ds_m")
    private Double ecDsM;

    @Column(name = "p2o5_mg")
    private Double p2o5Mg;

    @Column(name = "k2o_mg")
    private Double k2oMg;

    @Column(name = "cover_pct")
    private Double coverPct;

    @Column(name = "bare_area_pct")
    private Double bareAreaPct;

    private Double altitude;

    @Column(name = "bulk_density")
    private Double bulkDensity;

    @Column(name = "soc_storage_tc")
    private Double socStorageTc;

    // "Бага" | "Дунд" | "Их" | "Хэвийн" etc.
    private String talhagdal;

    @Column(name = "zone_num")
    private Integer zoneNum;

    // "GIS-62" | "GIS-180"
    @Column(name = "batch_code", length = 50)
    private String batchCode;
}
