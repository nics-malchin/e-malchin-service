package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.ZoneDAO;
import com.nics.e_malchin_service.Entity.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    @Autowired
    private ZoneDAO zoneDAO;

    public List<Zone> findAll() {
        return zoneDAO.findAll();
    }

    public Zone create(Zone zone) {
        return zoneDAO.save(zone);
    }

    public Zone update(Zone zone) {
        return zoneDAO.save(zone);
    }

    public void delete(Integer id) {
        zoneDAO.deleteById(id);
    }
}
