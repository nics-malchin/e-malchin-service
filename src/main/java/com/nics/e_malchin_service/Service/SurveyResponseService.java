package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.SurveyResponseRepository;
import com.nics.e_malchin_service.DAO.SurveySubmitDTO;
import com.nics.e_malchin_service.Entity.SurveyResponse;
import com.nics.e_malchin_service.Entity.SurveyResponseAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyResponseService {

    @Autowired
    private SurveyResponseRepository responseRepo;

    public SurveyResponse submitSurvey(SurveySubmitDTO dto) {
        SurveyResponse response = new SurveyResponse();
        response.setSurveyId(dto.getSurveyId());
        response.setUserId(dto.getUserId());

        List<SurveyResponseAnswer> answers = dto.getAnswers().stream().map(a -> {
            SurveyResponseAnswer ans = new SurveyResponseAnswer();
            ans.setQuestionId(a.getQuestionId());
            ans.setAnswerId(a.getAnswerId());
            ans.setAnswerText(a.getAnswerText());
            ans.setResponse(response);
            return ans;
        }).toList();

        response.setAnswers(answers);

        return responseRepo.save(response);
    }
}
