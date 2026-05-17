package com.nics.e_malchin_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackerDeviceDto {
    // Registry fields
    private Integer id;
    private String imei;
    private String uniqueId;       // alias of imei — keeps Traccar API compat on frontend
    private String name;
    private String deviceModel;
    private String notes;
    private Integer livestockId;
    private String livestockCode;
    private Boolean active;
    private LocalDateTime registeredAt;

    // Live telemetry (merged from gps_position)
    private String status;          // online / offline / unknown
    private LocalDateTime lastUpdate;
    private Double lastLatitude;
    private Double lastLongitude;
    private Double lastSpeed;
    private Integer lastSats;
    private Double batteryVolt;
    private Integer batteryPct;     // derived: (volt-3.5)/0.7*100 clamped 0-100
    private Integer signalLevel;    // raw RSSI 0-31
}
