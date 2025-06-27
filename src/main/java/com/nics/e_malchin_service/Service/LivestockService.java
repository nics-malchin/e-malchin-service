package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.LivestockType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    public Map<String, Object> getStats() {
        List<Livestock> all = livestockDAO.findAll();
        Map<String, Long> countByType = all.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().getName(),
                        Collectors.counting()
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", all.size());
        response.put("countByType", countByType);

        return response;
    }

    public List<LivestockType> getTypes() {
        return LivestockType.getAll();
    }
}
