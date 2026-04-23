package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.HorshooDAO;
import com.nics.e_malchin_service.Entity.Horshoo;
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


    // Horshoo шинээр нэмэх
    public Horshoo addHorshoo(Horshoo horshoo) {
        return horshooDAO.save(horshoo);
    }

    public Horshoo update(Horshoo updated) {
        Horshoo h = horshooDAO.findById(updated.getId())
                .orElseThrow(() -> new RuntimeException("Horshoo not found: " + updated.getId()));
        h.setName(updated.getName());
        h.setUsername(updated.getUsername());
        return horshooDAO.save(h);
    }

    public void delete(Integer id) {
        horshooDAO.deleteById(id);
    }

    public Horshoo findById(Integer id) {
        return horshooDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Horshoo not found"));
    }
}
