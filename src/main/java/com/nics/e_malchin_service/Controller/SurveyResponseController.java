package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.SurveySubmitDTO;
import com.nics.e_malchin_service.Entity.SurveyResponse;
import com.nics.e_malchin_service.Service.SurveyResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/survey")
public class SurveyResponseController {

    @Autowired
    private SurveyResponseService responseService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitSurvey(@RequestBody SurveySubmitDTO dto) {
        SurveyResponse response = responseService.submitSurvey(dto);
        return ResponseEntity.ok("Survey response saved with ID: " + response.getId());
    }
}
