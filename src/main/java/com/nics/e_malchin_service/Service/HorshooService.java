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

    // ID-аар хайх
    public Horshoo findById(Integer id) {
        return horshooDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Horshoo not found"));
    }
}
