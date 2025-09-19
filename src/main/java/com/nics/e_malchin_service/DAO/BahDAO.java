package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Bah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BahDAO extends JpaRepository<Bah, Integer> {

    @Query("select a from Bah a")
    List<Bah> findAll();

    Optional<Bah> findByUsername(String username);
}
