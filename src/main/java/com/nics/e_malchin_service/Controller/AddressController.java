package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Entity.City;
import com.nics.e_malchin_service.Entity.District;
import com.nics.e_malchin_service.Entity.Khoroo;
import com.nics.e_malchin_service.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/city")
    public ResponseEntity<City> createCity(@RequestBody City city) {
        return ResponseEntity.ok(addressService.createCity(city));
    }

    @PutMapping("/city/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer id, @RequestBody City city) {
        return ResponseEntity.ok(addressService.updateCity(id, city));
    }

    @DeleteMapping("/city/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        addressService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/district")
    public ResponseEntity<District> createDistrict(@RequestBody District district) {
        return ResponseEntity.ok(addressService.createDistrict(district));
    }

    @PutMapping("/district/{id}")
    public ResponseEntity<District> updateDistrict(@PathVariable Integer id, @RequestBody District district) {
        return ResponseEntity.ok(addressService.updateDistrict(id, district));
    }

    @DeleteMapping("/district/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Integer id) {
        addressService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/khoroo")
    public ResponseEntity<Khoroo> createKhoroo(@RequestBody Khoroo khoroo) {
        return ResponseEntity.ok(addressService.createKhoroo(khoroo));
    }

    @PutMapping("/khoroo/{id}")
    public ResponseEntity<Khoroo> updateKhoroo(@PathVariable Integer id, @RequestBody Khoroo khoroo) {
        return ResponseEntity.ok(addressService.updateKhoroo(id, khoroo));
    }

    @DeleteMapping("/khoroo/{id}")
    public ResponseEntity<Void> deleteKhoroo(@PathVariable Integer id) {
        addressService.deleteKhoroo(id);
        return ResponseEntity.noContent().build();
    }
}
