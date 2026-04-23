package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.AnimalHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnimalHealthRepository extends JpaRepository<AnimalHealth, Integer> {

    @Query("SELECT a FROM AnimalHealth a WHERE a.livestock.id = :livestockId")
    List<AnimalHealth> findByLivestockId(Integer livestockId);
}
