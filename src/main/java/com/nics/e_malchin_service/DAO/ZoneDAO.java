package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneDAO extends JpaRepository<Zone, Integer> {
    List<Zone> findByZoneType(String zoneType);
}
