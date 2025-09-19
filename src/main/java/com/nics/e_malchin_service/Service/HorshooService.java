package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.HorshooDAO;
import com.nics.e_malchin_service.Entity.Horshoo;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorshooService {

    @Autowired
    private HorshooDAO horshooDAO;

    public List<Horshoo> findAll(){
        return horshooDAO.findAll();
    }

    public Horshoo create(Horshoo horshoo) {
        EntityAuditUtil.applyCreateAuditValues(horshoo);
        return horshooDAO.save(horshoo);
    }

    public Horshoo update(Integer id, Horshoo request) {
        Horshoo existing = horshooDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horshoo not found with id " + id));

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            existing.setPassword(request.getPassword());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return horshooDAO.save(existing);
    }

    public void delete(Integer id) {
        if (!horshooDAO.existsById(id)) {
            throw new EntityNotFoundException("Horshoo not found with id " + id);
        }
        horshooDAO.deleteById(id);
    }
}
