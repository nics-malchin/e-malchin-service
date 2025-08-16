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

    public List<District> getAllDistrictByCityId(int cityId){
        return districtDAO.findDistrictsByCity_id(cityId);
    }

    public List<Khoroo> getAllKhoroo(int districtId){
        return khorooDAO.findKhoroosByDistrict_id(districtId);
    }

}
