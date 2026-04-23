package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LabResultDto {
    private Integer id;
    private Integer livestockId;
    private String livestockCode;
    private LocalDate sampleDate;
    private LocalDate resultDate;
    private String testType;
    private String labName;
    private String result;
    private Boolean certified;
    private String notes;
}
