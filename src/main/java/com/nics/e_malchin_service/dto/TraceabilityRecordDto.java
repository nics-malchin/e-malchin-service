package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TraceabilityRecordDto {
    private Integer id;
    private Integer livestockId;
    private String livestockCode;
    private Integer recordedById;
    private String recordedByName;
    private String stepType;
    private String description;
    private String evidenceRef;
    private LocalDateTime recordedAt;
}
