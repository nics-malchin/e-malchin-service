package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.DAO.SurveyDTO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.DAO.UserSignatureDAO;
import com.nics.e_malchin_service.Entity.*;
import com.nics.e_malchin_service.Service.BahService;
import com.nics.e_malchin_service.Service.HorshooService;
import com.nics.e_malchin_service.Service.LivestockService;
import com.nics.e_malchin_service.Service.*;
import com.nics.e_malchin_service.dto.AnimalHealthDto;
import com.nics.e_malchin_service.dto.GpsDeviceDto;
import com.nics.e_malchin_service.dto.KmzPolygonDto;
import com.nics.e_malchin_service.dto.LabResultDto;
import com.nics.e_malchin_service.dto.CertificateDto;
import com.nics.e_malchin_service.dto.TraceabilityRecordDto;
import com.nics.e_malchin_service.Entity.Zone;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private com.nics.e_malchin_service.DAO.SurveyResponseRepository surveyResponseRepository;
    @Autowired
    private ZoneService zoneService;
    @Autowired
    private AnimalHealthService animalHealthService;
    @Autowired
    private GpsDeviceService gpsDeviceService;
    @Autowired
    private LabResultService labResultService;
    @Autowired
    private SoilSurveyPointService soilSurveyPointService;
    @Autowired
    private CertificateService certificateService;
    @Autowired
    private TraceabilityRecordService traceabilityRecordService;

    @GetMapping("/bah/getAll")
    public ResponseEntity<?> getAllBah() {
        return ResponseEntity.ok(bahService.findAll());
    }

    /** Horshoo sees only their BAHs; admin sees all. */
    @GetMapping("/bah/my-bahs")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public ResponseEntity<?> getMyBahs(@AuthenticationPrincipal Jwt principal,
                                       org.springframework.security.core.Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        if (isAdmin) {
            return ResponseEntity.ok(bahService.findAll());
        }
        String callerUsername = principal.getClaim("preferred_username");
        Optional<User> callerOpt = userDAO.findByUsername(callerUsername);
        if (callerOpt.isEmpty() || callerOpt.get().getHorshoo_id() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(bahService.findByHorshooId(callerOpt.get().getHorshoo_id()));
    }

    @PostMapping("/bah/create")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public ResponseEntity<?> createBah(@RequestBody Bah bah) {
        bah.setCreatedBy(0);
        return ResponseEntity.ok(bahService.addBah(bah));
    }

    @PostMapping("/bah/update")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public ResponseEntity<?> updateBah(@RequestBody Bah bah) {
        return ResponseEntity.ok(bahService.update(bah));
    }

    @DeleteMapping("/bah/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteBah(@PathVariable Integer id) {
        bahService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/horshoo/getAll")
    public ResponseEntity<?> getAllHorshoo() {
        return ResponseEntity.ok(horshooService.findAll());
    }

    @PostMapping("/horshoo/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createHorshoo(@RequestBody Horshoo horshoo) {
        horshoo.setCreatedBy(0);
        return ResponseEntity.ok(horshooService.addHorshoo(horshoo));
    }

    @PostMapping("/horshoo/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateHorshoo(@RequestBody Horshoo horshoo) {
        return ResponseEntity.ok(horshooService.update(horshoo));
    }

    @DeleteMapping("/horshoo/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteHorshoo(@PathVariable Integer id) {
        horshooService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/getAll")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * BAH → returns users with matching bah_id.
     * Horshoo → returns users with matching horshoo_id.
     * Admin → returns all users.
     */
    @GetMapping("/user/my-malchins")
    @PreAuthorize("hasAnyRole('admin','bah','horshoo')")
    public ResponseEntity<?> getMyMalchins(@AuthenticationPrincipal Jwt principal,
                                           org.springframework.security.core.Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        if (isAdmin) {
            return ResponseEntity.ok(userService.findAll());
        }

        String callerUsername = principal.getClaim("preferred_username");
        Optional<User> callerOpt = userDAO.findByUsername(callerUsername);
        if (callerOpt.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        User caller = callerOpt.get();

        boolean isHorshoo = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_horshoo"));
        if (isHorshoo && caller.getHorshoo_id() != null) {
            return ResponseEntity.ok(userService.findByHorshooId(caller.getHorshoo_id()));
        }

        if (caller.getBah_id() != null) {
            return ResponseEntity.ok(userService.findByBahId(caller.getBah_id()));
        }

        return ResponseEntity.ok(List.of());
    }

    @DeleteMapping("/user/delete/{id}")
    @PreAuthorize("hasAnyRole('admin','bah','horshoo')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("bahCount", (long) bahService.findAll().size());
        stats.put("horshooCount", (long) horshooService.findAll().size());
        stats.put("livestockCount", livestockService.getTotalCount());
        stats.put("animalHealthCount", animalHealthService.findAll().size());
        stats.put("labResultCount", labResultService.findAll().size());
        stats.put("gpsDeviceCount", gpsDeviceService.findAll().size());
        stats.put("certificateCount", certificateService.findAll().size());
        stats.put("traceabilityCount", traceabilityRecordService.findAll().size());
        stats.put("soilSurveyCount", soilSurveyPointService.findAll().size());
        return ResponseEntity.ok(stats);
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

    @GetMapping("/livestock/getAll")
    @PreAuthorize("hasAnyRole('admin','horshoo')")
    public List<Livestock> getAllLivestock() {
        return livestockService.getAllLivestock();
    }

    @PostMapping("/livestock/create")
    public ResponseEntity<Livestock> create(@RequestBody Livestock l) {
        return ResponseEntity.ok(livestockService.create(l));
    }

//    @PostMapping("/livestock/register")
//    public ResponseEntity<?> register(@RequestParam Long userId,
//                                      @RequestParam int type,
//                                      @RequestParam int quantity) {
//        return livestockService.registerLivestock(userId, type, quantity);
//    }
    @PostMapping("/livestock/update")
    public ResponseEntity<Livestock> update(@RequestBody Livestock l) {
        return ResponseEntity.ok(livestockService.update(l));
    }

    @DeleteMapping("/livestock/{id}")
    @PreAuthorize("hasAnyRole('admin','bah','horshoo')")
    public ResponseEntity<?> deleteLivestock(@PathVariable Integer id) {
        livestockService.delete(id);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<?> getAllSurvey(@RequestParam(required = false) Long userId) {
        List<Survey> surveys = surveyService.findSurveysMalchin();

        List<SurveyDTO> dto = surveys.stream().map(s -> {
            SurveyDTO d = modelMapper.map(s, SurveyDTO.class);
            d.setTotalQuestions(s.getQuestions() != null ? s.getQuestions().size() : 0);
            if (userId != null) {
                d.setAnswered(surveyResponseRepository.existsBySurveyIdAndUserId((long) s.getId(), userId));
            }
            return d;
        }).toList();

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

    @DeleteMapping("/survey/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteSurvey(@PathVariable int id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/survey/{id}/responses")
    public ResponseEntity<?> getSurveyResponses(@PathVariable Long id) {
        List<SurveyResponse> responses = surveyResponseRepository.findBySurveyId(id);
        List<Map<String, Object>> result = responses.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", r.getUserId());
            String userName = null;
            try {
                User u = userDAO.findById(r.getUserId());
                if (u != null) {
                    String full = ((u.getLastName() == null ? "" : u.getLastName()) + " "
                                 + (u.getFirstName() == null ? "" : u.getFirstName())).trim();
                    userName = full.isEmpty() ? u.getUsername() : full;
                }
            } catch (Exception ignored) {}
            m.put("userName", userName);
            m.put("createdAt", r.getSubmittedAt());
            m.put("completed", true);
            List<Map<String, Object>> ans =
                (r.getAnswers() == null ? List.<SurveyResponseAnswer>of() : r.getAnswers())
                    .stream().map(a -> {
                        Map<String, Object> am = new HashMap<>();
                        am.put("questionId", a.getQuestionId());
                        am.put("answerId", a.getAnswerId());
                        am.put("answer", a.getAnswerText());
                        return am;
                    }).toList();
            m.put("answers", ans);
            return m;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/livestock/getInfoById")
    public ResponseEntity<?> getInfoById(@RequestParam("id") int id) {
        return ResponseEntity.ok(livestockService.findByLivestockId(id));
    }

    // ─── Zone CRUD ───────────────────────────────────────────────────────────

    @GetMapping("/zone/getAll")
    public ResponseEntity<?> getAllZones() {
        return ResponseEntity.ok(zoneService.findAll());
    }

    @GetMapping("/zone/geojson")
    public ResponseEntity<?> getZonesAsGeoJson(@RequestParam(required = false) String zoneType) {
        return ResponseEntity.ok(zoneService.toGeoJson(zoneType));
    }

    @PostMapping("/zone/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createZone(@RequestBody Zone zone) {
        zone.setCreatedBy(0);
        return ResponseEntity.ok(zoneService.create(zone));
    }

    @PostMapping("/zone/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateZone(@RequestBody Zone zone) {
        return ResponseEntity.ok(zoneService.update(zone));
    }

    @DeleteMapping("/zone/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteZone(@PathVariable Integer id) {
        zoneService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Accepts a KMZ or KML file, parses all Polygon placemarks,
     * and returns the polygon list WITHOUT saving to the database.
     * The frontend shows a preview dialog; the user then calls batch-create to confirm.
     */
    @PostMapping("/zone/parse-kmz")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> parseKmzFile(@RequestParam("file") MultipartFile file) {
        try {
            List<KmzPolygonDto> polygons = zoneService.parseKmz(file);
            return ResponseEntity.ok(polygons);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Файл уншихад алдаа гарлаа: " + e.getMessage()));
        }
    }

    /**
     * Saves a list of zones in a single transaction.
     * Used after the frontend import preview dialog is confirmed.
     */
    @PostMapping("/zone/batch-create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> batchCreateZones(@RequestBody List<Zone> zones) {
        if (zones == null || zones.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Хадгалах бүс байхгүй байна."));
        }
        return ResponseEntity.ok(zoneService.batchCreate(zones));
    }

    // ─── AnimalHealth CRUD ───────────────────────────────────────────────────

    @GetMapping("/animal-health/getAll")
    public ResponseEntity<?> getAllAnimalHealth() {
        return ResponseEntity.ok(animalHealthService.findAll());
    }

    @GetMapping("/animal-health/byLivestock")
    public ResponseEntity<?> getAnimalHealthByLivestock(@RequestParam Integer livestockId) {
        return ResponseEntity.ok(animalHealthService.findByLivestock(livestockId));
    }

    @PostMapping("/animal-health/create")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> createAnimalHealth(@RequestBody AnimalHealthDto dto) {
        return ResponseEntity.ok(animalHealthService.create(dto));
    }

    @PostMapping("/animal-health/update")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> updateAnimalHealth(@RequestBody AnimalHealthDto dto) {
        return ResponseEntity.ok(animalHealthService.update(dto));
    }

    @DeleteMapping("/animal-health/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteAnimalHealth(@PathVariable Integer id) {
        animalHealthService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ─── GpsDevice CRUD ─────────────────────────────────────────────────────

    @GetMapping("/gps-device/getAll")
    public ResponseEntity<?> getAllGpsDevices() {
        return ResponseEntity.ok(gpsDeviceService.findAll());
    }

    @GetMapping("/gps-device/byLivestock")
    public ResponseEntity<?> getGpsDeviceByLivestock(@RequestParam Integer livestockId) {
        return ResponseEntity.ok(gpsDeviceService.findByLivestock(livestockId));
    }

    @PostMapping("/gps-device/create")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> createGpsDevice(@RequestBody GpsDeviceDto dto) {
        return ResponseEntity.ok(gpsDeviceService.create(dto));
    }

    @PostMapping("/gps-device/update")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> updateGpsDevice(@RequestBody GpsDeviceDto dto) {
        return ResponseEntity.ok(gpsDeviceService.update(dto));
    }

    @DeleteMapping("/gps-device/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteGpsDevice(@PathVariable Integer id) {
        gpsDeviceService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ─── LabResult CRUD ─────────────────────────────────────────────────────

    @GetMapping("/lab-result/getAll")
    public ResponseEntity<?> getAllLabResults() {
        return ResponseEntity.ok(labResultService.findAll());
    }

    @GetMapping("/lab-result/byLivestock")
    public ResponseEntity<?> getLabResultByLivestock(@RequestParam Integer livestockId) {
        return ResponseEntity.ok(labResultService.findByLivestock(livestockId));
    }

    @PostMapping("/lab-result/create")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> createLabResult(@RequestBody LabResultDto dto) {
        return ResponseEntity.ok(labResultService.create(dto));
    }

    @PostMapping("/lab-result/update")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> updateLabResult(@RequestBody LabResultDto dto) {
        return ResponseEntity.ok(labResultService.update(dto));
    }

    @DeleteMapping("/lab-result/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteLabResult(@PathVariable Integer id) {
        labResultService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ─── SoilSurvey CRUD ────────────────────────────────────────────────────

    @GetMapping("/soil-survey/getAll")
    public ResponseEntity<?> getAllSoilSurvey() {
        return ResponseEntity.ok(soilSurveyPointService.findAll());
    }

    @GetMapping("/soil-survey/geojson")
    public ResponseEntity<?> getSoilSurveyGeoJson() {
        return ResponseEntity.ok(soilSurveyPointService.toGeoJson());
    }

    @PostMapping("/soil-survey/create")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> createSoilSurvey(@RequestBody com.nics.e_malchin_service.Entity.SoilSurveyPoint p) {
        return ResponseEntity.ok(soilSurveyPointService.create(p));
    }

    @PostMapping("/soil-survey/update")
    @PreAuthorize("hasAnyRole('admin','bah')")
    public ResponseEntity<?> updateSoilSurvey(@RequestBody com.nics.e_malchin_service.Entity.SoilSurveyPoint p) {
        return ResponseEntity.ok(soilSurveyPointService.update(p));
    }

    @DeleteMapping("/soil-survey/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteSoilSurvey(@PathVariable Integer id) {
        soilSurveyPointService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ─── Certificate CRUD ───────────────────────────────────────────────────

    @GetMapping("/certificate/getAll")
    public ResponseEntity<?> getAllCertificates() {
        return ResponseEntity.ok(certificateService.findAll());
    }

    @GetMapping("/certificate/byLivestock")
    public ResponseEntity<?> getCertificatesByLivestock(@RequestParam Integer livestockId) {
        return ResponseEntity.ok(certificateService.findByLivestock(livestockId));
    }

    @PostMapping("/certificate/create")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> createCertificate(@RequestBody CertificateDto dto) {
        return ResponseEntity.ok(certificateService.create(dto));
    }

    @PostMapping("/certificate/update")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> updateCertificate(@RequestBody CertificateDto dto) {
        return ResponseEntity.ok(certificateService.update(dto));
    }

    @DeleteMapping("/certificate/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteCertificate(@PathVariable Integer id) {
        certificateService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ─── TraceabilityRecord CRUD ─────────────────────────────────────────────

    @GetMapping("/traceability/getAll")
    public ResponseEntity<?> getAllTraceability() {
        return ResponseEntity.ok(traceabilityRecordService.findAll());
    }

    @GetMapping("/traceability/byLivestock")
    public ResponseEntity<?> getTraceabilityByLivestock(@RequestParam Integer livestockId) {
        return ResponseEntity.ok(traceabilityRecordService.findByLivestock(livestockId));
    }

    @PostMapping("/traceability/create")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> createTraceability(@RequestBody TraceabilityRecordDto dto) {
        return ResponseEntity.ok(traceabilityRecordService.create(dto));
    }

    @PostMapping("/traceability/update")
    @PreAuthorize("hasAnyRole('admin','bah','vet')")
    public ResponseEntity<?> updateTraceability(@RequestBody TraceabilityRecordDto dto) {
        return ResponseEntity.ok(traceabilityRecordService.update(dto));
    }

    @DeleteMapping("/traceability/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteTraceability(@PathVariable Integer id) {
        traceabilityRecordService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@AuthenticationPrincipal Jwt principal, @RequestBody Map<String, String> body) {
        String username = principal.getClaim("preferred_username");
        System.out.println("Username: " + username);

        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(body.get("password"));
        user.setFirstName(body.get("firstName"));
        user.setLastName(body.get("lastName"));

        user.setPin(body.get("pin"));


        // Auto-inherit bah/horshoo from caller; explicit body values override afterward.
        Optional<User> caller = userDAO.findByUsername(username);
        if (caller.isPresent()) {
            user.setBah_id(caller.get().getBah_id());
            user.setHorshoo_id(caller.get().getHorshoo_id());
        }
        if (body.containsKey("bah_id") && !body.get("bah_id").isBlank())
            user.setBah_id(Integer.parseInt(body.get("bah_id")));
        if (body.containsKey("horshoo_id") && !body.get("horshoo_id").isBlank())
            user.setHorshoo_id(Integer.parseInt(body.get("horshoo_id")));

        if (body.containsKey("phone_number")) user.setPhone_number(Integer.parseInt(body.get("phone_number")));
        if (body.containsKey("family_id")) user.setFamily_id(Integer.parseInt(body.get("family_id")));
        if (body.containsKey("aimag_id")) user.setAimag_id(Integer.parseInt(body.get("aimag_id")));
        if (body.containsKey("sum_id")) user.setSum_id(Integer.parseInt(body.get("sum_id")));
        if (body.containsKey("bag_id")) user.setBag_id(Integer.parseInt(body.get("bag_id")));
        if (body.containsKey("location_description")) user.setLocation_description(Integer.parseInt(body.get("location_description")));
        if (body.containsKey("herder_count")) user.setHerder_count(Integer.parseInt(body.get("herder_count")));
        if (body.containsKey("family_count")) user.setFamily_count(Integer.parseInt(body.get("family_count")));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        user.setIs_license_approved(0);

        String role = body.get("role");
        String organizationName = body.getOrDefault("organization_name", null);
        User created = userRegistrationService.registerUser(user, role, organizationName);
        return ResponseEntity.ok(created);
    }
}
