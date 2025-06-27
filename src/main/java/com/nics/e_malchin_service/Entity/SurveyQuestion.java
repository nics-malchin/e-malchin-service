package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "survey_questions", schema = "nics")
public class SurveyQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String question;

    private String type;

    private Integer survey_id;

    @OneToMany
    @JoinColumn(name = "question_id")
    private List<SurveyAnswer> answer;
}

