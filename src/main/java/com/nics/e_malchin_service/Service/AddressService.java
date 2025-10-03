package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.CityDAO;
import com.nics.e_malchin_service.DAO.DistrictDAO;
import com.nics.e_malchin_service.DAO.KhorooDAO;
import com.nics.e_malchin_service.Entity.City;
import com.nics.e_malchin_service.Entity.District;
import com.nics.e_malchin_service.Entity.Khoroo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private CityDAO cityDAO;
    @Autowired
    private DistrictDAO districtDAO;
    @Autowired
    private KhorooDAO khorooDAO;

    public List<City> getAllCity(){
        return cityDAO.findAll();
    }
    public City getCityById(int id){
        return cityDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
    }

    public List<District> getAllDistrictByCityId(int cityId){
        return districtDAO.findDistrictsByCity_id(cityId);
    }

    public District getDistrictById(int id){
        return districtDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("District not found"));
    }

    public List<Khoroo> getAllKhoroo(int cityId, int districtId){
        return khorooDAO.findKhoroosByCity_idAndDistrict_id(cityId, districtId);
    }

    public Khoroo getKhorooById(int id){
        return khorooDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Khoroo not found"));
    }

}
