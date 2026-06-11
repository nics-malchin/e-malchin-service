package com.nics.e_malchin_service.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "bah", schema = "nics")
public class Bah extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String username;

    private String password;

    private Integer horshoo_id;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "bah_id")
    private List<User> userList;
}

