package clinicalappointment.controller;

import clinicalappointment.model.Appointment;
import clinicalappointment.model.Hospital;
import clinicalappointment.model.Patient;
import clinicalappointment.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final HospitalService hospitalService;

    public AppointmentController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/hospitals")
    public List<Hospital> listHospitals() {
        return hospitalService.getAllHospitals();
    }

    @GetMapping("/hospitals/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable int id) {
        Hospital h = hospitalService.getHospitalById(id);
        if (h == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(h);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Patient patient) {
        if (patient == null || patient.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        Hospital best = hospitalService.findNearestHospital(patient.getX(), patient.getY());
        if (best == null) return ResponseEntity.status(503).build();
        Appointment appt = new Appointment(patient, best);
        return ResponseEntity.ok(appt);
    }
}