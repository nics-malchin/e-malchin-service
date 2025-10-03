package com.nics.e_malchin_service.DAO;

import lombok.Data;

import java.util.List;

@Data
public class SurveySubmitDTO {
    private Long surveyId;
    private Long userId;
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String answerText;
        private Long answerId;
    }
}
