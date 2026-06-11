package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.RoleMenuConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleMenuConfigRepository extends JpaRepository<RoleMenuConfig, Integer> {
    List<RoleMenuConfig> findByRoleName(String roleName);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoleMenuConfig r WHERE r.roleName = :roleName")
    void deleteByRoleName(@Param("roleName") String roleName);

    boolean existsByRoleName(String roleName);
}
