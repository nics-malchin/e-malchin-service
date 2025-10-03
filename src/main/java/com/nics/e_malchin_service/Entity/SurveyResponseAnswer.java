package com.nics.e_malchin_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "survey_response_answer")
public class SurveyResponseAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "response_id")
    private SurveyResponse response;

    private Long questionId;

    @Column(name = "answer_id")
    private Long answerId;

    private String answerText;
}

