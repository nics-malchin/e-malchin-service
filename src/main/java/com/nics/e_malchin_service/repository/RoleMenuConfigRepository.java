package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.RoleMenuConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleMenuConfigRepository extends JpaRepository<RoleMenuConfig, Integer> {
    List<RoleMenuConfig> findByRoleName(String roleName);
    void deleteByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
}
