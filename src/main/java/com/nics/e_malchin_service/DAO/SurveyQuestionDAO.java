package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyQuestionDAO extends JpaRepository<SurveyQuestion, Integer> {

    @Query("select a from SurveyQuestion a")
    List<SurveyQuestion> findAll();

    @Query("select a from SurveyQuestion a where a.survey.id = ?1")
    List<SurveyQuestion> findBySurveyId(int surveyId);
}
