package clinicalappointment.service;

import clinicalappointment.model.Hospital;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalService {
    private final List<Hospital> hospitals = new ArrayList<>();

    public HospitalService() {
        // seed some sample hospitals
        hospitals.add(new Hospital(1, "Central Hospital", 0.0, 0.0));
        hospitals.add(new Hospital(2, "North Clinic", 0.0, 10.0));
        hospitals.add(new Hospital(3, "East Health Center", 10.0, 0.0));
    }

    public List<Hospital> getAllHospitals() {
        return hospitals;
    }

    public Hospital getHospitalById(int id) {
        return hospitals.stream().filter(h -> h.getNodeId() == id).findFirst().orElse(null);
    }

    public Hospital findNearestHospital(double x, double y) {
        Hospital best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Hospital h : hospitals) {
            double dx = h.getX() - x;
            double dy = h.getY() - y;
            double d = dx*dx + dy*dy;
            if (d < bestDist) {
                bestDist = d;
                best = h;
            }
        }
        return best;
    }
}
