package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Horshoo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HorshooDAO extends JpaRepository<Horshoo, Integer> {

    @Query("select a from Horshoo a")
    List<Horshoo> findAll();
}
