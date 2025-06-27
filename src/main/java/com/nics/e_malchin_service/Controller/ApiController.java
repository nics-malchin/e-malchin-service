package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.LivestockType;
import com.nics.e_malchin_service.Entity.User;
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

    @GetMapping("/horshoo/getAll")
    public ResponseEntity<?> getAllHorshoo() {
        return ResponseEntity.ok(horshooService.findAll());
    }

    @GetMapping("/notification/getAll")
    public ResponseEntity<?> getAllNotification() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/user/getAll")
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok(userService.findAll());
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
        User user = userDAO.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setBah_id(updatedUser.getBah_id());
        user.setHorshoo_id(updatedUser.getHorshoo_id());

        userDAO.save(user);
        return ResponseEntity.ok(user);
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

    @GetMapping("/survey/getQuestions")
    public ResponseEntity<?> getAllSurveyAnswer(@RequestParam("surveyId") int surveyId) {
        return ResponseEntity.ok(surveyService.findQuestionsBySurveyId(surveyId));
    }
}
