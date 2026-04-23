package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CertificateDto {
    private Integer id;
    private Integer livestockId;
    private String livestockCode;
    private Integer issuedToId;
    private String issuedToName;
    private String certNumber;
    private String certType;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String certStatus;
    private String notes;
}
