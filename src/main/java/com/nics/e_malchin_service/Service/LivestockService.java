package com.nics.e_malchin_service.Service;

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

    @Autowired
    LivestockDAO livestockDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    private SurveyDAO surveyDAO;

    public List<Livestock> getAll(int id) {
        return livestockDAO.findByUserId(id);
    }

    public Livestock create(Livestock livestock) {
        livestock.setCreatedBy(1000);
        livestock.setView("");
        Double weight = 0.0;
        livestock.setWeight(weight);
        livestock.setUser(userDAO.findById(livestock.getUserId()).get());

        return livestockDAO.save(livestock);
    }

    @Transactional
    public Livestock update(Livestock updated) {
        Livestock livestock = livestockDAO.findById(updated.getId()).get();
        livestock.setCreatedBy(1000);
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
