package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "livestock", schema = "nics")
public class Livestock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private Integer age;

    private Double weight;

    @Enumerated(EnumType.ORDINAL)
    private LivestockType type;

    private String view;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    private Integer sex;
    private Integer livestock_id;
    private Integer parent_id;
    private Integer have_child;
    private Integer health_id;


    @Transient
    private Integer userId;
}

