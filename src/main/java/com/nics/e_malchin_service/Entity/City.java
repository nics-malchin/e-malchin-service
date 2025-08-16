package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "city", schema = "nics")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

//    @OneToMany
//    @JoinColumn(name = "city_id")
//    private List<District> districts;
}
