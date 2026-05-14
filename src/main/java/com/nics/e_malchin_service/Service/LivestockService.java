package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.CityDAO;
import com.nics.e_malchin_service.DAO.DistrictDAO;
import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.SurveyDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.LivestockType;
import com.nics.e_malchin_service.Entity.Survey;
import com.nics.e_malchin_service.Entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivestockService {

    @Autowired LivestockDAO livestockDAO;
    @Autowired UserDAO userDAO;
    @Autowired CityDAO cityDAO;
    @Autowired DistrictDAO districtDAO;
    @Autowired SurveyDAO surveyDAO;

    public List<Livestock> getAll(int id) {
        return livestockDAO.findByUserId(id);
    }

    public List<Livestock> getAllLivestock() {
        return livestockDAO.findAll();
    }

    public Livestock create(Livestock livestock) {
        livestock.setCreatedBy(livestock.getUserId());
        livestock.setView("");
        User owner = userDAO.findById(livestock.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + livestock.getUserId()));
        livestock.setUser(owner);
        livestock.setCode(generateCode(owner));
        return livestockDAO.save(livestock);
    }

    // Формат: AASSNNNNNN (10 оронтой, тасхийм)
    // АА = city.code (аймаг, 2 оронтой)
    // СС = district.code (сум, 2 оронтой)
    // NNNNNN = малын дараалал (6 оронтой, 000001-ээс эхэлнэ)
    // Жишээ: 1416000001
    private String generateCode(User owner) {
        String aimag = cityDAO.findById(owner.getAimag_id() != null ? owner.getAimag_id() : 0)
                .map(c -> String.format("%2s", c.getCode()).replace(' ', '0'))
                .orElse("00");

        String sum = districtDAO.findById(owner.getSum_id() != null ? owner.getSum_id() : 0)
                .map(d -> String.format("%2s", d.getCode()).replace(' ', '0'))
                .orElse("00");

        int seq = livestockDAO.findByUserId(owner.getId()).size() + 1;

        return aimag + sum + String.format("%06d", seq);
    }

    public void delete(Integer id) {
        livestockDAO.deleteById(id);
    }

    @Transactional
    public Livestock update(Livestock updated) {
        Livestock livestock = livestockDAO.findById(updated.getId())
                .orElseThrow(() -> new RuntimeException("Livestock not found: " + updated.getId()));
        livestock.setAge(updated.getAge());
        livestock.setType(updated.getType());
        livestock.setCode(updated.getCode());
        return livestockDAO.save(livestock);
    }

    public Map<String, Object> getStats(int userId) {
        List<Livestock> all = livestockDAO.findByUserId(userId);
        Map<String, Long> countByType = all.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().getName(),
                        Collectors.counting()
                ));

        List<Survey> surveyList = surveyDAO.findAll();


        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", all.size());
        response.put("countByType", countByType);
        response.put("surveyCount", surveyList.size());

        return response;
    }

    public long getTotalCount() {
        return livestockDAO.count();
    }

    public List<LivestockType> getTypes() {
        return LivestockType.getAll();
    }

    public Livestock findByLivestockId(Integer id) {
        return livestockDAO.findByLivestock_id(id);
    }

//    @Transactional
//    public ResponseEntity<String> registerLivestock(Long userId, int type, int quantity) {
//        // 1) Малчны мэдээллийг user-ээс авна
//        User user = userDAO.findById(userId);
//
//        String provinceCode = String.valueOf(user.getAimag_id());  // User.java-аас авна
//        String districtCode = String.valueOf(user.getSum_id());
//
//        String prefix = provinceCode + districtCode;
//
//
//        Long lastCode = Long.decode(livestockDAO.findLastCodeByPrefix(Long.decode(prefix)));
//        // 2) Сүүлийн кодыг авах
//        for (int i = 1; i <= quantity+1; i++) {
//            Long newCode = lastCode + i;
//
//            Livestock livestock = new Livestock();
//            livestock.setCode(newCode);
//            livestock.setType(LivestockType.fromCode(type));
//            livestock.setUser(user);
//            livestockDAO.save(livestock);
//        }
//        return ResponseEntity.ok("Бүртгэж дууслаа.");
//    }
}
