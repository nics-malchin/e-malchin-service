package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityDAO extends JpaRepository<City, Integer> {

    @Query("select a from City a")
    List<City> findAll();
}
