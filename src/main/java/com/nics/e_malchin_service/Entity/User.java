package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users", schema = "nics")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String pin;

    private String username;

    private String password;

    private Integer bah_id;

    private Integer horshoo_id;

    private Integer is_license_approved;

    private Integer phone_number;
    private Integer family_id;
    private Integer aimag_id;
    private Integer sum_id;
    private Integer bag_id;
    private Integer location_description;
    private Integer herder_count;
    private Integer family_count;


    @Transient
    private Integer email;
    @Transient
    private Integer role;


}

