package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.SurveyAnswerDAO;
import com.nics.e_malchin_service.DAO.SurveyDAO;
import com.nics.e_malchin_service.DAO.SurveyQuestionDAO;
import com.nics.e_malchin_service.Entity.Survey;
import com.nics.e_malchin_service.Entity.SurveyAnswer;
import com.nics.e_malchin_service.Entity.SurveyQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyService {

    @Autowired
    private SurveyDAO surveyDAO;
    @Autowired
    private SurveyQuestionDAO surveyquestionDAO;
    @Autowired
    private SurveyAnswerDAO surveyanswerDAO;

    public List<Survey> findSurveysMalchin(){
        return surveyDAO.findAllByType("malchin");
    }
    public List<SurveyQuestion> findQuestionsBySurveyId(int surveyId){
        return surveyquestionDAO.findBySurveyId(surveyId);
    }


}
