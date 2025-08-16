package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DistrictDAO extends JpaRepository<District, Integer> {

    @Query("select a from District a")
    List<District> findAll();

    @Query("select a from District a where a.city_id = ?1")
    List<District> findDistrictsByCity_id(int cityId);
}
