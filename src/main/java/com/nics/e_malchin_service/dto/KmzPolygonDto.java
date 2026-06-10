package com.nics.e_malchin_service.dto;

import lombok.Data;

@Data
public class KmzPolygonDto {
    private String name;
    private String description;
    /** JSON array: [[lat,lng], [lat,lng], ...] — same format as Zone.coordinates */
    private String coordinates;
    private String category;
    private String color;
}
