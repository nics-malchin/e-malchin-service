package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.CityDAO;
import com.nics.e_malchin_service.DAO.DistrictDAO;
import com.nics.e_malchin_service.DAO.KhorooDAO;
import com.nics.e_malchin_service.Entity.City;
import com.nics.e_malchin_service.Entity.District;
import com.nics.e_malchin_service.Entity.Khoroo;
import jakarta.persistence.EntityNotFoundException;
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

    public City createCity(City city) {
        return cityDAO.save(city);
    }

    public City updateCity(Integer id, City request) {
        City existing = cityDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id " + id));

        if (request.getCode() != null) {
            existing.setCode(request.getCode());
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }

        return cityDAO.save(existing);
    }

    public void deleteCity(Integer id) {
        if (!cityDAO.existsById(id)) {
            throw new EntityNotFoundException("City not found with id " + id);
        }
        cityDAO.deleteById(id);
    }

    public District createDistrict(District district) {
        return districtDAO.save(district);
    }

    public District updateDistrict(Integer id, District request) {
        District existing = districtDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("District not found with id " + id));

        if (request.getCode() != null) {
            existing.setCode(request.getCode());
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getCity_id() != 0) {
            existing.setCity_id(request.getCity_id());
        }

        return districtDAO.save(existing);
    }

    public void deleteDistrict(Integer id) {
        if (!districtDAO.existsById(id)) {
            throw new EntityNotFoundException("District not found with id " + id);
        }
        districtDAO.deleteById(id);
    }

    public Khoroo createKhoroo(Khoroo khoroo) {
        return khorooDAO.save(khoroo);
    }

    public Khoroo updateKhoroo(Integer id, Khoroo request) {
        Khoroo existing = khorooDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khoroo not found with id " + id));

        if (request.getCode() != null) {
            existing.setCode(request.getCode());
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getDistrict_id() != 0) {
            existing.setDistrict_id(request.getDistrict_id());
        }

        return khorooDAO.save(existing);
    }

    public void deleteKhoroo(Integer id) {
        if (!khorooDAO.existsById(id)) {
            throw new EntityNotFoundException("Khoroo not found with id " + id);
        }
        khorooDAO.deleteById(id);
    }
}
