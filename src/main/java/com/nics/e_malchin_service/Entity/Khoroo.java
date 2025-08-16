package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "khoroo", schema = "nics")
public class Khoroo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private Integer city_id;

    private Integer district_id;
}
