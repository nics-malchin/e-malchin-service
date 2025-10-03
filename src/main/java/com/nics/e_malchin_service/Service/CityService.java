package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.CityDAO;
import com.nics.e_malchin_service.Entity.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    CityDAO cityDAO;

    public List<City> getAllCity(){
        return cityDAO.findAll();
    }
    public City getCityById(int id){
        return cityDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
    }
    public City addCity(City city){
        return cityDAO.save(city);
    }
    public City updateCity(City city){
        return cityDAO.save(city);
    }

}
