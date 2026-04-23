package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "lab_result", schema = "nics")
public class LabResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    private LocalDate sampleDate;
    private LocalDate resultDate;

    @Column(name = "test_type", length = 100)
    private String testType;

    @Column(name = "lab_name", length = 200)
    private String labName;

    @Column(length = 100)
    private String result;

    private Boolean certified;

    @Column(length = 1000)
    private String notes;

    @Transient
    private Integer livestockId;
}
