package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyAnswerDAO extends JpaRepository<SurveyAnswer, Integer> {

    @Query("select a from SurveyAnswer a")
    List<SurveyAnswer> findAll();
}
