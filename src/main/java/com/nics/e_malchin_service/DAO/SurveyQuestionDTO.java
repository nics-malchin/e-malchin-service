package com.nics.e_malchin_service.DAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyQuestionDTO {
    private Integer id;
    private String question;
    private String type;
    private List<SurveyAnswerDTO> answers;
}

