package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.User;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public List<User> findAll(){
        return userDAO.findAll();
    }

    public User create(User user) {
        EntityAuditUtil.applyCreateAuditValues(user);
        return userDAO.save(user);
    }

    public User update(Integer id, User request) {
        User existing = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existing.setLastName(request.getLastName());
        }
        if (request.getPin() != null) {
            existing.setPin(request.getPin());
        }
        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            existing.setPassword(request.getPassword());
        }
        if (request.getBah_id() != null) {
            existing.setBah_id(request.getBah_id());
        }
        if (request.getHorshoo_id() != null) {
            existing.setHorshoo_id(request.getHorshoo_id());
        }
        if (request.getIs_license_approved() != null) {
            existing.setIs_license_approved(request.getIs_license_approved());
        }
        if (request.getPhone_number() != null) {
            existing.setPhone_number(request.getPhone_number());
        }
        if (request.getFamily_id() != null) {
            existing.setFamily_id(request.getFamily_id());
        }
        if (request.getAimag_id() != null) {
            existing.setAimag_id(request.getAimag_id());
        }
        if (request.getSum_id() != null) {
            existing.setSum_id(request.getSum_id());
        }
        if (request.getBag_id() != null) {
            existing.setBag_id(request.getBag_id());
        }
        if (request.getLocation_description() != null) {
            existing.setLocation_description(request.getLocation_description());
        }
        if (request.getHerder_count() != null) {
            existing.setHerder_count(request.getHerder_count());
        }
        if (request.getFamily_count() != null) {
            existing.setFamily_count(request.getFamily_count());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return userDAO.save(existing);
    }

    public void delete(Integer id) {
        if (!userDAO.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        userDAO.deleteById(id);
    }

    public User findById(Integer id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }
}
