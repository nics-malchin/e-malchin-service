package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.SurveyAnswerDAO;
import com.nics.e_malchin_service.DAO.SurveyDAO;
import com.nics.e_malchin_service.DAO.SurveyQuestionDAO;
import com.nics.e_malchin_service.Entity.Survey;
import com.nics.e_malchin_service.Entity.SurveyAnswer;
import com.nics.e_malchin_service.Entity.SurveyQuestion;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
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

    public Survey createSurvey(Survey survey) {
        EntityAuditUtil.applyCreateAuditValues(survey);
        return surveyDAO.save(survey);
    }

    public Survey updateSurvey(Integer id, Survey request) {
        Survey existing = surveyDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Survey not found with id " + id));

        if (request.getSurveyTitle() != null) {
            existing.setSurveyTitle(request.getSurveyTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getEndDate() != null) {
            existing.setEndDate(request.getEndDate());
        }
        if (request.getType() != null) {
            existing.setType(request.getType());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return surveyDAO.save(existing);
    }

    public void deleteSurvey(Integer id) {
        if (!surveyDAO.existsById(id)) {
            throw new EntityNotFoundException("Survey not found with id " + id);
        }
        surveyDAO.deleteById(id);
    }

    public SurveyQuestion createQuestion(SurveyQuestion question) {
        EntityAuditUtil.applyCreateAuditValues(question);
        return surveyquestionDAO.save(question);
    }

    public SurveyQuestion updateQuestion(Integer id, SurveyQuestion request) {
        SurveyQuestion existing = surveyquestionDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Survey question not found with id " + id));

        if (request.getQuestion() != null) {
            existing.setQuestion(request.getQuestion());
        }
        if (request.getType() != null) {
            existing.setType(request.getType());
        }
        if (request.getSurvey_id() != null) {
            existing.setSurvey_id(request.getSurvey_id());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return surveyquestionDAO.save(existing);
    }

    public void deleteQuestion(Integer id) {
        if (!surveyquestionDAO.existsById(id)) {
            throw new EntityNotFoundException("Survey question not found with id " + id);
        }
        surveyquestionDAO.deleteById(id);
    }

    public SurveyAnswer createAnswer(SurveyAnswer answer) {
        EntityAuditUtil.applyCreateAuditValues(answer);
        return surveyanswerDAO.save(answer);
    }

    public SurveyAnswer updateAnswer(Integer id, SurveyAnswer request) {
        SurveyAnswer existing = surveyanswerDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Survey answer not found with id " + id));

        if (request.getQuestion_id() != null) {
            existing.setQuestion_id(request.getQuestion_id());
        }
        if (request.getText() != null) {
            existing.setText(request.getText());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return surveyanswerDAO.save(existing);
    }

    public void deleteAnswer(Integer id) {
        if (!surveyanswerDAO.existsById(id)) {
            throw new EntityNotFoundException("Survey answer not found with id " + id);
        }
        surveyanswerDAO.deleteById(id);
    }
}
