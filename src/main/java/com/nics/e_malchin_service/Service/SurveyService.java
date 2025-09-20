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


//    public Survey createSurvey(Survey survey) {
//        survey.setCreatedBy(1000);
//        survey.setType("malchin");
//        return surveyDAO.save(survey);
//    }
//
//    public List<SurveyQuestion> addQuestions(int surveyId, List<SurveyQuestion> questions) {
//        questions.forEach(q -> {
//            q.setSurvey_id(surveyId);
//            q.setCreatedBy(1000);
//            q.setType("singleChoice");
//            surveyquestionDAO.save(q);
//        });
//        return questions;
//    }
//
//    public List<SurveyAnswer> addAnswers(int questionId, List<SurveyAnswer> answers) {
//        answers.forEach(a -> {
//            a.setQuestion_id(questionId);
//            a.setCreatedBy(1000);
//            surveyanswerDAO.save(a);
//        });
//        return answers;
//    }

    public Survey createSurveyWithQuestions(Survey survey) {
        // Эхлээд survey дээр default утгууд онооно
        survey.setCreatedBy(1000);
        survey.setType("malchin");

        if (survey.getQuestions() != null) {
            survey.getQuestions().forEach(q -> {
                q.setSurvey(survey);
                q.setCreatedBy(1000);
                q.setType("singleChoice");

                if (q.getAnswers() != null) {
                    q.getAnswers().forEach(a -> {
                        a.setQuestion(q);
                        a.setCreatedBy(1000);
                    });
                }
            });
        }

        // Cascade.ALL байгаа тул зөвхөн survey-г save хийхэд асуулт, хариултууд бүгд хадгалагдана
        return surveyDAO.save(survey);
    }


}
