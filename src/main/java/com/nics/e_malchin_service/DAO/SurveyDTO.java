package com.nics.e_malchin_service.DAO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SurveyDTO {
    private Long id;
    private String surveyTitle;
    private String description;
    private LocalDate endDate;
    private List<QuestionDTO> questions;
}

