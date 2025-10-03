package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.SurveyDTO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.DAO.UserSignatureDAO;
import com.nics.e_malchin_service.Entity.*;
import com.nics.e_malchin_service.Service.BahService;
import com.nics.e_malchin_service.Service.HorshooService;
import com.nics.e_malchin_service.Service.LivestockService;
import com.nics.e_malchin_service.Service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @Autowired
    UserRegistrationService userRegistrationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserSignatureDAO userSignatureDAO;

    @GetMapping("/bah/getAll")
    public ResponseEntity<?> getAllBah() {
        return ResponseEntity.ok(bahService.findAll());
    }

    @GetMapping("/horshoo/getAll")
    public ResponseEntity<?> getAllHorshoo() {
        return ResponseEntity.ok(horshooService.findAll());
    }

    @GetMapping("/user/getAll")
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/notification/getAll")
    public ResponseEntity<?> getAllNotification() {
        return ResponseEntity.ok(notificationService.findAll());
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

    @PostMapping("/livestock/register")
    public ResponseEntity<?> register(@RequestParam Long userId,
                                      @RequestParam int type,
                                      @RequestParam int quantity) {
        return livestockService.registerLivestock(userId, type, quantity);
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
    public ResponseEntity<?> getStats(@RequestParam("userId") int userId) {
        return ResponseEntity.ok(livestockService.getStats(userId));
    }

    @GetMapping("/survey/getAll")
    public ResponseEntity<?> getAllSurvey() {
        List<Survey> surveys = surveyService.findSurveysMalchin();

        List<SurveyDTO> dto = surveys.stream()
                .map(s -> modelMapper.map(s, SurveyDTO.class))
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/survey/getQuestions")
    public ResponseEntity<?> getAllSurveyAnswer(@RequestParam("surveyId") int surveyId) {
        return ResponseEntity.ok(surveyService.findQuestionsBySurveyId(surveyId));
    }

    @PostMapping("/survey/createFull")
    public ResponseEntity<Survey> createSurveyWithQuestions(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.createSurveyWithQuestions(survey));
    }




    @GetMapping("/livestock/getInfoById")
    public ResponseEntity<?> getInfoById(@RequestParam("id") int id) {
        return ResponseEntity.ok(livestockService.findByLivestockId(id));
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> body) {
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(body.get("password"));
        user.setFirstName(body.get("firstName"));
        user.setLastName(body.get("lastName"));

        user.setPin(body.get("pin"));

        if (body.containsKey("bahId") && body.get("bahId") != null && !body.get("bahId").equals("")) {
            user.setBah_id(Integer.parseInt(body.get("bahId")));
        }
        if (body.containsKey("horshooId") && body.get("horshooId") != null && !body.get("horshooId").equals("" )) {
            user.setHorshoo_id(Integer.parseInt(body.get("horshooId")));
        }

        if (body.containsKey("phone_number")) user.setPhone_number(Integer.parseInt(body.get("phone_number")));
        if (body.containsKey("family_id")) user.setFamily_id(Integer.parseInt(body.get("family_id")));
        if (body.containsKey("aimag_id")) user.setAimag_id(Integer.parseInt(body.get("aimag_id")));
        if (body.containsKey("sum_id")) user.setSum_id(Integer.parseInt(body.get("sum_id")));
        if (body.containsKey("bag_id")) user.setBag_id(Integer.parseInt(body.get("bag_id")));
        if (body.containsKey("location_description")) user.setLocation_description(Integer.parseInt(body.get("location_description")));
        if (body.containsKey("herder_count")) user.setHerder_count(Integer.parseInt(body.get("herder_count")));
        if (body.containsKey("family_count")) user.setFamily_count(Integer.parseInt(body.get("family_count")));
        if (body.containsKey("is_license_approved")) user.setIs_license_approved(Integer.parseInt(body.get("is_license_approved")));

        String role = body.get("role");
        User created = userRegistrationService.registerUser(user, role);
        return ResponseEntity.ok(created);
    }


}
