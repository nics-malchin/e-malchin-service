package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Livestock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

//    @Query("SELECT l.code FROM Livestock l " +
//            "WHERE SUBSTRING(l.code, 1, 4) = :prefix " +
//            "ORDER BY l.code DESC LIMIT 1")
//    String findLastCodeByPrefix(@Param("prefix") Long prefix);
}
