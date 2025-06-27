package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.BahDAO;
import com.nics.e_malchin_service.DAO.LivestockDAO;
import com.nics.e_malchin_service.Entity.Bah;
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
}
