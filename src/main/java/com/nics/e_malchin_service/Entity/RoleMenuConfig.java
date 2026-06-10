package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role_menu_config", schema = "nics",
       uniqueConstraints = @UniqueConstraint(columnNames = {"role_name", "menu_key"}))
public class RoleMenuConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "menu_key", nullable = false, length = 100)
    private String menuKey;
}
