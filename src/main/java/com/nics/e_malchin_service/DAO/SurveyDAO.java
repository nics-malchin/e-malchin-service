package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyDAO extends JpaRepository<Survey, Integer> {

    @Query("select a from Survey a")
    List<Survey> findAll();

    List<Survey> findAllByType(String malchin);
}
