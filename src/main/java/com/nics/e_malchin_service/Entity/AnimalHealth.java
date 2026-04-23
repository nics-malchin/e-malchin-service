package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "animal_health", schema = "nics")
public class AnimalHealth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosed_by")
    private User diagnosedBy;

    private LocalDate examinationDate;

    @Column(length = 500)
    private String symptoms;

    @Column(length = 500)
    private String diagnosis;

    @Column(length = 500)
    private String treatment;

    private LocalDate nextCheckDate;

    @Column(length = 1000)
    private String notes;

    @Transient
    private Integer livestockId;

    @Transient
    private Integer diagnosedById;
}
