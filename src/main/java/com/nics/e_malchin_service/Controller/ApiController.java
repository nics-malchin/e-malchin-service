package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.*;
import com.nics.e_malchin_service.Service.BahService;
import com.nics.e_malchin_service.Service.HorshooService;
import com.nics.e_malchin_service.Service.LivestockService;
import com.nics.e_malchin_service.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    BahService bahService;
    @Autowired
    HorshooService horshooService;
    @Autowired
    LivestockService livestockService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    UserService userService;
    @Autowired
    SurveyService surveyService;
    @Autowired
    UserDAO userDAO;

    @GetMapping("/bah/getAll")
    public ResponseEntity<?> getAllBah() {
        return ResponseEntity.ok(bahService.findAll());
    }

    @PostMapping("/bah")
    public ResponseEntity<Bah> createBah(@RequestBody Bah bah) {
        return ResponseEntity.ok(bahService.create(bah));
    }

    @PutMapping("/bah/{id}")
    public ResponseEntity<Bah> updateBah(@PathVariable Integer id, @RequestBody Bah bah) {
        return ResponseEntity.ok(bahService.update(id, bah));
    }

    @DeleteMapping("/bah/{id}")
    public ResponseEntity<Void> deleteBah(@PathVariable Integer id) {
        bahService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/horshoo/getAll")
    public ResponseEntity<?> getAllHorshoo() {
        return ResponseEntity.ok(horshooService.findAll());
    }

    @PostMapping("/horshoo")
    public ResponseEntity<Horshoo> createHorshoo(@RequestBody Horshoo horshoo) {
        return ResponseEntity.ok(horshooService.create(horshoo));
    }

    @PutMapping("/horshoo/{id}")
    public ResponseEntity<Horshoo> updateHorshoo(@PathVariable Integer id, @RequestBody Horshoo horshoo) {
        return ResponseEntity.ok(horshooService.update(id, horshoo));
    }

    @DeleteMapping("/horshoo/{id}")
    public ResponseEntity<Void> deleteHorshoo(@PathVariable Integer id) {
        horshooService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notification/getAll")
    public ResponseEntity<?> getAllNotification() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @PostMapping("/notification")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.create(notification));
    }

    @PutMapping("/notification/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable Integer id, @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.update(id, notification));
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/getAll")
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }
    @GetMapping("/user/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("preferred_username");
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.update(updatedUser.getId(), updatedUser));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.update(id, updatedUser));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/livestock")
    public List<Livestock> getAll(@RequestParam("userId") int userId) {
        return livestockService.getAll(userId);
    }

    @PostMapping("/livestock/create")
    public ResponseEntity<Livestock> create(@RequestBody Livestock l) {
        return ResponseEntity.ok(livestockService.create(l));
    }

    @PostMapping("/livestock/update")
    public ResponseEntity<Livestock> update(@RequestBody Livestock l) {

        return ResponseEntity.ok(livestockService.update(l));
    }

    @DeleteMapping("/livestock/{id}")
    public ResponseEntity<Void> deleteLivestock(@PathVariable Integer id) {
        livestockService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/livestock/types")
    public List<LivestockType> getTypes() {
        return livestockService.getTypes();
    }

    @GetMapping("/livestock/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(livestockService.getStats());
    }

    @GetMapping("/survey/getAll")
    public ResponseEntity<?> getAllSurvey() {
        return ResponseEntity.ok(surveyService.findSurveysMalchin());
    }

    @PostMapping("/survey")
    public ResponseEntity<Survey> createSurvey(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.createSurvey(survey));
    }

    @PutMapping("/survey/{id}")
    public ResponseEntity<Survey> updateSurvey(@PathVariable Integer id, @RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.updateSurvey(id, survey));
    }

    @DeleteMapping("/survey/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Integer id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/survey/getQuestions")
    public ResponseEntity<?> getAllSurveyAnswer(@RequestParam("surveyId") int surveyId) {
        return ResponseEntity.ok(surveyService.findQuestionsBySurveyId(surveyId));
    }

    @PostMapping("/survey/question")
    public ResponseEntity<SurveyQuestion> createSurveyQuestion(@RequestBody SurveyQuestion question) {
        return ResponseEntity.ok(surveyService.createQuestion(question));
    }

    @PutMapping("/survey/question/{id}")
    public ResponseEntity<SurveyQuestion> updateSurveyQuestion(@PathVariable Integer id, @RequestBody SurveyQuestion question) {
        return ResponseEntity.ok(surveyService.updateQuestion(id, question));
    }

    @DeleteMapping("/survey/question/{id}")
    public ResponseEntity<Void> deleteSurveyQuestion(@PathVariable Integer id) {
        surveyService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/survey/answer")
    public ResponseEntity<SurveyAnswer> createSurveyAnswer(@RequestBody SurveyAnswer answer) {
        return ResponseEntity.ok(surveyService.createAnswer(answer));
    }

    @PutMapping("/survey/answer/{id}")
    public ResponseEntity<SurveyAnswer> updateSurveyAnswer(@PathVariable Integer id, @RequestBody SurveyAnswer answer) {
        return ResponseEntity.ok(surveyService.updateAnswer(id, answer));
    }

    @DeleteMapping("/survey/answer/{id}")
    public ResponseEntity<Void> deleteSurveyAnswer(@PathVariable Integer id) {
        surveyService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/livestock/getInfoById")
    public ResponseEntity<?> getInfoById(@RequestParam("id") int id) {
        return ResponseEntity.ok(livestockService.findByLivestockId(id));
    }
}
