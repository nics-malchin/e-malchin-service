package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Livestock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LivestockDAO extends JpaRepository<Livestock, Integer> {

    @Query("select a from Livestock a")
    List<Livestock> findAll();


    @Query("select a from Livestock a where a.user.id = ?1")
    List<Livestock> findByUserId(Integer userId);


    @Query("select a from Livestock a where a.type = ?1")
    List<Livestock> findByType(Integer type);

    @Query("select a from Livestock a where a.livestock_id = ?1")
    Livestock findByLivestock_id(Integer id);
}
