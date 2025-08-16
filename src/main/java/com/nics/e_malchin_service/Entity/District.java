package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "district", schema = "nics")
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private int city_id;

//    @OneToMany
//    @JoinColumn(name = "district_id")
//    private List<Khoroo> khoroos;
}
