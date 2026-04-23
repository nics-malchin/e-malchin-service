package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "certificate", schema = "nics")
public class Certificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id")
    private Livestock livestock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_to")
    private User issuedTo;

    @Column(name = "cert_number", length = 100)
    private String certNumber;

    @Column(name = "cert_type", length = 100)
    private String certType;

    private LocalDate issuedDate;
    private LocalDate expiryDate;

    @Column(length = 50)
    private String certStatus;

    @Column(length = 1000)
    private String notes;

    @Transient
    private Integer livestockId;

    @Transient
    private Integer issuedToId;
}
