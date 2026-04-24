package com.nics.e_malchin_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nics.e_malchin_service.DAO.ZoneDAO;
import com.nics.e_malchin_service.Entity.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ZoneService {

    @Autowired
    private ZoneDAO zoneDAO;

    private final ObjectMapper mapper = new ObjectMapper();

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

    // Returns a GeoJSON FeatureCollection for Leaflet/frontend map rendering.
    // coordinates stored as [[lat,lng],...] → converted to GeoJSON [[lng,lat],...]
    public Map<String, Object> toGeoJson(String zoneType) {
        List<Zone> zones = zoneType != null
                ? zoneDAO.findByZoneType(zoneType)
                : zoneDAO.findAll();

        List<Map<String, Object>> features = new ArrayList<>();
        for (Zone z : zones) {
            try {
                List<List<Double>> latLngPairs = mapper.readValue(z.getCoordinates(),
                        mapper.getTypeFactory().constructCollectionType(List.class,
                                mapper.getTypeFactory().constructCollectionType(List.class, Double.class)));

                // Swap [lat,lng] → [lng,lat] for GeoJSON
                List<List<Double>> lngLat = new ArrayList<>();
                for (List<Double> pt : latLngPairs) {
                    lngLat.add(Arrays.asList(pt.get(1), pt.get(0)));
                }

                Map<String, Object> geometry = new LinkedHashMap<>();
                geometry.put("type", "Polygon");
                geometry.put("coordinates", List.of(lngLat));

                Map<String, Object> props = new LinkedHashMap<>();
                props.put("id", z.getId());
                props.put("name", z.getName());
                props.put("zoneNum", z.getZoneNum());
                props.put("bahName", z.getBahName());
                props.put("areaHa", z.getAreaHa());
                props.put("zoneType", z.getZoneType());
                props.put("category", z.getCategory());
                props.put("color", z.getColor());

                Map<String, Object> feature = new LinkedHashMap<>();
                feature.put("type", "Feature");
                feature.put("properties", props);
                feature.put("geometry", geometry);
                features.add(feature);
            } catch (Exception ignored) {
            }
        }

        Map<String, Object> fc = new LinkedHashMap<>();
        fc.put("type", "FeatureCollection");
        fc.put("features", features);
        return fc;
    }
}
