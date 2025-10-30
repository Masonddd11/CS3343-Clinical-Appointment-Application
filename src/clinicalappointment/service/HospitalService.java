package clinicalappointment.service;

import clinicalappointment.model.Hospital;
import clinicalappointment.model.HospitalEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private List<Hospital> hospitals = new ArrayList<>();

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
        // load hospitals from DB; if empty, seed and save
        List<HospitalEntity> entities = hospitalRepository.findAll();
        if (entities.isEmpty()) {
            seedDefaults();
        } else {
            this.hospitals = entities.stream().map(this::toModel).collect(Collectors.toList());
        }
    }

    private void seedDefaults() {
        // Try to load resource data/hospitals_ha.json
        try (var is = getClass().getClassLoader().getResourceAsStream("data/hospitals_ha.json")) {
            if (is != null) {
                java.nio.charset.Charset utf8 = java.nio.charset.StandardCharsets.UTF_8;
                String json = new String(is.readAllBytes(), utf8);
                // parse JSON array
                var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                var node = mapper.readTree(json);
                if (node.isArray()) {
                    List<HospitalEntity> list = new java.util.ArrayList<>();
                    for (var item : node) {
                        String name = item.path("institution_eng").asText();
                        double lat = item.path("latitude").asDouble(0.0);
                        double lon = item.path("longitude").asDouble(0.0);
                        String district = item.path("address_eng").asText("");
                        String cluster = item.path("cluster_eng").asText("");
                        String region = "";
                        // infer region from cluster or address
                        if (cluster != null && cluster.toLowerCase().contains("hong kong")) region = "Hong Kong Island";
                        if (cluster != null && cluster.toLowerCase().contains("kowloon")) region = "Kowloon";
                        if (cluster != null && cluster.toLowerCase().contains("new territories") || cluster != null && cluster.toLowerCase().contains("new territory")) region = "New Territories";
                        if (district != null && district.toLowerCase().contains("lantau")) region = "Lantau Island";

                        HospitalEntity e = new HospitalEntity(name, lat, lon, district, region);
                        list.add(e);
                    }
                    List<HospitalEntity> saved = hospitalRepository.saveAll(list);
                    this.hospitals = saved.stream().map(this::toModel).collect(Collectors.toList());
                    return;
                }
            }
        } catch (Exception ex) {
            // fall back to embedded seed
            ex.printStackTrace();
        }

        // If resource not found or failed, use previous inline seeds
        List<HospitalEntity> seed = List.of(
                new HospitalEntity("Queen Mary Hospital", 22.2855, 114.1355, "Pok Fu Lam", "Hong Kong Island"),
                new HospitalEntity("Queen Elizabeth Hospital", 22.3200, 114.1840, "Kowloon", "Kowloon"),
                new HospitalEntity("Prince of Wales Hospital", 22.3810, 114.1916, "Sha Tin", "New Territories"),
                new HospitalEntity("Tuen Mun Hospital", 22.3845, 113.9732, "Tuen Mun", "New Territories"),
                new HospitalEntity("Pamela Youde Nethersole Eastern Hospital", 22.2833, 114.2414, "Chai Wan", "Hong Kong Island"),
                new HospitalEntity("Caritas Medical Centre", 22.3316, 114.1709, "Sham Shui Po", "Kowloon"),
                new HospitalEntity("Alice Ho Miu Ling Nethersole Hospital", 22.4943, 114.1216, "Tai Po", "New Territories"),
                new HospitalEntity("Kowloon Hospital", 22.3249, 114.1805, "Kowloon City", "Kowloon"),
                new HospitalEntity("Ruttonjee Hospital", 22.2770, 114.1652, "Wan Chai", "Hong Kong Island"),
                new HospitalEntity("Princess Margaret Hospital", 22.3373, 114.1607, "Kwai Chung", "New Territories"),
                new HospitalEntity("United Christian Hospital", 22.3106, 114.2136, "Kowloon East", "Kowloon"),
                new HospitalEntity("Tseung Kwan O Hospital", 22.3206, 114.2601, "Tseung Kwan O", "New Territories"),
                new HospitalEntity("Yan Chai Hospital", 22.3719, 114.1186, "Tsuen Wan", "New Territories"),
                new HospitalEntity("Kwong Wah Hospital", 22.3155, 114.1717, "Mong Kok", "Kowloon"),
                new HospitalEntity("North District Hospital", 22.5029, 114.1324, "Tai Po", "New Territories"),
                new HospitalEntity("Pok Oi Hospital", 22.4441, 114.0217, "Yuen Long", "New Territories"),
                new HospitalEntity("St. Teresa's Hospital", 22.2797, 114.1704, "Causeway Bay", "Hong Kong Island"),
                new HospitalEntity("Nethersole Hospital (Tai Po)", 22.4510, 114.1680, "Tai Po", "New Territories"),
                new HospitalEntity("Tung Wah Hospital", 22.2809, 114.1581, "Sheung Wan", "Hong Kong Island")
        );
        List<HospitalEntity> saved = hospitalRepository.saveAll(seed);
        this.hospitals = saved.stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<Hospital> getAllHospitals() {
        return hospitals;
    }

    public Hospital getHospitalById(int id) {
        return hospitals.stream().filter(h -> h.getNodeId() == id).findFirst().orElse(null);
    }

    // Haversine distance (meters) using lat/lon stored in x (lat) and y (lon)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // metres
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dphi/2) * Math.sin(dphi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(dlambda/2) * Math.sin(dlambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public Hospital findNearestHospital(double x, double y) {
        Hospital best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Hospital h : hospitals) {
            double d = haversine(x, y, h.getX(), h.getY());
            if (d < bestDist) {
                bestDist = d;
                best = h;
            }
        }
        return best;
    }

    // Add a hospital. If the provided hospital has nodeId == 0, assign a new id.
    public Hospital addHospital(Hospital h) {
        HospitalEntity entity = new HospitalEntity(h.getName(), h.getX(), h.getY(), h.getDistrict(), null);
        HospitalEntity saved = hospitalRepository.save(entity);
        Hospital model = toModel(saved);
        hospitals.add(model);
        return model;
    }

    public boolean deleteHospitalById(int id) {
        boolean removed = hospitals.removeIf(h -> h.getNodeId() == id);
        if (removed) {
            hospitalRepository.deleteById(id);
        }
        return removed;
    }

    private Hospital toModel(HospitalEntity e) {
        return new Hospital(e.getId(), e.getName(), e.getLat(), e.getLon(), e.getDistrict());
    }
}
