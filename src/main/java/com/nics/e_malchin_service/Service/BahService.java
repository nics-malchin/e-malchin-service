package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.BahDAO;
import com.nics.e_malchin_service.Entity.Bah;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BahService {

    @Autowired
    private BahDAO bahDAO;

    public List<Bah> findAll(){
        return bahDAO.findAll();
    }

    public Bah create(Bah bah) {
        EntityAuditUtil.applyCreateAuditValues(bah);
        return bahDAO.save(bah);
    }

    public Bah update(Integer id, Bah request) {
        Bah existing = bahDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bah not found with id " + id));

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            existing.setPassword(request.getPassword());
        }
        if (request.getHorshoo_id() != null) {
            existing.setHorshoo_id(request.getHorshoo_id());
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return bahDAO.save(existing);
    }

    public void delete(Integer id) {
        if (!bahDAO.existsById(id)) {
            throw new EntityNotFoundException("Bah not found with id " + id);
        }
        bahDAO.deleteById(id);
    }
}
