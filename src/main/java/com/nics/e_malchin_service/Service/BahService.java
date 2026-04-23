package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.BahDAO;
import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.Entity.Bah;
import com.nics.e_malchin_service.Entity.User;
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


    // Bah шинээр нэмэх
    public Bah addBah(Bah bah) {
        return bahDAO.save(bah);
    }

    public Bah update(Bah updated) {
        Bah b = bahDAO.findById(updated.getId())
                .orElseThrow(() -> new RuntimeException("Bah not found: " + updated.getId()));
        b.setName(updated.getName());
        b.setUsername(updated.getUsername());
        b.setHorshoo_id(updated.getHorshoo_id());
        return bahDAO.save(b);
    }

    public void delete(Integer id) {
        bahDAO.deleteById(id);
    }

    public Bah findById(Integer id) {
        return bahDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Bah not found"));
    }
}
