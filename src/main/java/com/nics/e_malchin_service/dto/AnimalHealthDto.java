package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AnimalHealthDto {
    private Integer id;
    private Integer livestockId;
    private String livestockCode;
    private Integer diagnosedById;
    private String diagnosedByName;
    private LocalDate examinationDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private LocalDate nextCheckDate;
    private String notes;
}
