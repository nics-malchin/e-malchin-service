package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Livestock;
import com.nics.e_malchin_service.Entity.LivestockType;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
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
        livestock.setView(livestock.getView() == null ? "" : livestock.getView());
        if (livestock.getWeight() == null) {
            livestock.setWeight(0.0);
        }
        if (livestock.getUserId() != null) {
            livestock.setUser(userDAO.findById(livestock.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + livestock.getUserId())));
        }
        EntityAuditUtil.applyCreateAuditValues(livestock);
        return livestockDAO.save(livestock);
    }

    @Transactional
    public Livestock update(Livestock updated) {
        Livestock livestock = livestockDAO.findById(updated.getId())
                .orElseThrow(() -> new EntityNotFoundException("Livestock not found with id " + updated.getId()));

        if (updated.getAge() != null) {
            livestock.setAge(updated.getAge());
        }
        if (updated.getType() != null) {
            livestock.setType(updated.getType());
        }
        if (updated.getCode() != null) {
            livestock.setCode(updated.getCode());
        }
        if (updated.getWeight() != null) {
            livestock.setWeight(updated.getWeight());
        }
        if (updated.getView() != null) {
            livestock.setView(updated.getView());
        }
        if (updated.getUserId() != null) {
            livestock.setUser(userDAO.findById(updated.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + updated.getUserId())));
        }
        if (updated.getSex() != null) {
            livestock.setSex(updated.getSex());
        }
        if (updated.getLivestock_id() != null) {
            livestock.setLivestock_id(updated.getLivestock_id());
        }
        if (updated.getParent_id() != null) {
            livestock.setParent_id(updated.getParent_id());
        }
        if (updated.getHave_child() != null) {
            livestock.setHave_child(updated.getHave_child());
        }
        if (updated.getHealth_id() != null) {
            livestock.setHealth_id(updated.getHealth_id());
        }

        EntityAuditUtil.applyUpdateAuditValues(livestock, updated.getUpdatedBy());
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

    public Livestock findByLivestockId(Integer id) {
        return livestockDAO.findByLivestock_id(id);
    }

    public void delete(Integer id) {
        if (!livestockDAO.existsById(id)) {
            throw new EntityNotFoundException("Livestock not found with id " + id);
        }
        livestockDAO.deleteById(id);
    }
}
