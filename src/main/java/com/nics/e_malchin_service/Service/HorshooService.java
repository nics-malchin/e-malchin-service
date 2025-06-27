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
}
