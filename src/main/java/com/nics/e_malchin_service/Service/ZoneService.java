package com.nics.e_malchin_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nics.e_malchin_service.DAO.ZoneDAO;
import com.nics.e_malchin_service.Entity.Zone;
import com.nics.e_malchin_service.dto.KmzPolygonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZoneService {

    @Autowired
    private ZoneDAO zoneDAO;

    private final ObjectMapper mapper = new ObjectMapper();

    // ─── Basic CRUD ───────────────────────────────────────────────────────────

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

    @Transactional
    public List<Zone> batchCreate(List<Zone> zones) {
        zones.forEach(z -> z.setCreatedBy(0));
        return zoneDAO.saveAll(zones);
    }

    // ─── KMZ / KML parsing ───────────────────────────────────────────────────

    /**
     * Accepts a .kmz or .kml file, parses all Polygon placemarks,
     * and returns a list of DTOs ready for the frontend preview dialog.
     * Nothing is saved to the database.
     */
    public List<KmzPolygonDto> parseKmz(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase() : "";

        String kmlContent;
        if (filename.endsWith(".kmz")) {
            kmlContent = extractKmlFromKmz(file.getBytes());
        } else if (filename.endsWith(".kml")) {
            kmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IllegalArgumentException("Зөвхөн .kmz эсвэл .kml файл дэмжигдэнэ.");
        }

        return parseKmlContent(kmlContent);
    }

    private String extractKmlFromKmz(byte[] bytes) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".kml")) {
                    return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        throw new IllegalArgumentException("KMZ файлд KML файл олдсонгүй.");
    }

    private List<KmzPolygonDto> parseKmlContent(String kmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        // Disable external entity resolution (XXE protection)
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(kmlContent)));
        doc.getDocumentElement().normalize();

        List<KmzPolygonDto> result = new ArrayList<>();
        NodeList placemarks = doc.getElementsByTagName("Placemark");

        for (int i = 0; i < placemarks.getLength(); i++) {
            Element pm = (Element) placemarks.item(i);
            String name = getDirectChildText(pm, "name");
            if (name == null || name.isBlank()) name = "Нэргүй бүс";
            String description = getDirectChildText(pm, "description");

            NodeList polygons = pm.getElementsByTagName("Polygon");
            for (int j = 0; j < polygons.getLength(); j++) {
                Element poly = (Element) polygons.item(j);
                String coords = extractOuterRingCoords(poly);
                if (coords == null) continue;

                KmzPolygonDto dto = new KmzPolygonDto();
                dto.setName(polygons.getLength() > 1 ? name + " (" + (j + 1) + ")" : name);
                dto.setDescription(description != null ? description : "");
                dto.setCoordinates(coords);
                dto.setCategory("Бэлчээр");
                dto.setColor("#27AE60");
                result.add(dto);
            }
        }

        return result;
    }

    /**
     * Extracts the outer boundary ring coordinates from a &lt;Polygon&gt; element.
     * KML format: "lon,lat,alt lon,lat,alt ..."
     * Stored format: [[lat,lng], [lat,lng], ...]
     */
    private String extractOuterRingCoords(Element polygon) throws Exception {
        // Prefer outerBoundaryIs > LinearRing > coordinates
        NodeList outerBoundary = polygon.getElementsByTagName("outerBoundaryIs");
        Element scope = outerBoundary.getLength() > 0 ? (Element) outerBoundary.item(0) : polygon;

        NodeList coordNodes = scope.getElementsByTagName("coordinates");
        if (coordNodes.getLength() == 0) return null;

        String raw = coordNodes.item(0).getTextContent().trim();
        List<List<Double>> latLngPairs = new ArrayList<>();

        for (String triplet : raw.split("\\s+")) {
            if (!triplet.contains(",")) continue;
            String[] parts = triplet.split(",");
            if (parts.length < 2) continue;
            try {
                double lng = Double.parseDouble(parts[0].trim());
                double lat = Double.parseDouble(parts[1].trim());
                latLngPairs.add(Arrays.asList(lat, lng));
            } catch (NumberFormatException ignored) {}
        }

        if (latLngPairs.size() < 3) return null;
        return mapper.writeValueAsString(latLngPairs);
    }

    /** Returns text content of the first direct-ish child element with the given tag name. */
    private String getDirectChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    // ─── GeoJSON export ───────────────────────────────────────────────────────

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
            } catch (Exception ignored) {}
        }

        Map<String, Object> fc = new LinkedHashMap<>();
        fc.put("type", "FeatureCollection");
        fc.put("features", features);
        return fc;
    }
}
