package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GpsDeviceDto {
    private Integer id;
    private Integer livestockId;
    private String livestockCode;
    private String deviceCode;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private LocalDateTime recordedAt;
    private Integer batteryLevel;
}
