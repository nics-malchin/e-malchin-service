package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "traceability_record", schema = "nics")
public class TraceabilityRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @Column(name = "step_type", length = 100)
    private String stepType;

    @Column(length = 1000)
    private String description;

    @Column(name = "evidence_ref", length = 200)
    private String evidenceRef;

    private LocalDateTime recordedAt;

    @Transient
    private Integer livestockId;

    @Transient
    private Integer recordedById;
}
