package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Entity.City;
import com.nics.e_malchin_service.Entity.District;
import com.nics.e_malchin_service.Entity.Khoroo;
import com.nics.e_malchin_service.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    AddressService addressService;
    @RequestMapping("/getAllCity")
    public List<City> getAllCity(){
        return addressService.getAllCity();
    }
    @RequestMapping("/getAllDistrict")
    public List<District> getAllDistrict(@RequestParam("cityId") int cityId){
        return addressService.getAllDistrictByCityId(cityId);
    }
    @RequestMapping("/getAllKhoroo")
    public List<Khoroo> getAllKhoroo(@RequestParam("districtId") int districtId){
        return addressService.getAllKhoroo(districtId);
    }
}
