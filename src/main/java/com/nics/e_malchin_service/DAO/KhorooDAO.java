package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Khoroo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KhorooDAO extends JpaRepository<Khoroo, Integer> {

    @Query("select a from Khoroo a")
    List<Khoroo> findAll();

    @Query("select a from Khoroo a where a.district_id = ?1")
    List<Khoroo> findKhoroosByDistrict_id(int districtId);
}
