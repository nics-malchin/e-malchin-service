package com.nics.e_malchin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackfillResultDto {
    private long totalLines;
    private long totalParsed;
    private long totalInserted;
    private long totalSkipped;
    private long durationMs;
}
