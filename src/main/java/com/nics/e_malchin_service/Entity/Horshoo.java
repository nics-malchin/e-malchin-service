package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "horshoo", schema = "nics")
public class Horshoo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String username;

    private String password;

    @OneToMany
    @JoinColumn(name = "horshoo_id")
    private List<Bah> bahList;
}

