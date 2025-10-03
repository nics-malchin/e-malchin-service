package com.nics.e_malchin_service.DAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyAnswerDTO {
    private Integer id;
    private String answer;
}
