package clinicalappointment.controller;

import clinicalappointment.model.Appointment;
import clinicalappointment.model.Hospital;
import clinicalappointment.model.Patient;
import clinicalappointment.service.HospitalService;
import clinicalappointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final HospitalService hospitalService;
    private final AppointmentService appointmentService;

    public AppointmentController(HospitalService hospitalService, AppointmentService appointmentService) {
        this.hospitalService = hospitalService;
        this.appointmentService = appointmentService;
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
        Appointment appt = appointmentService.createAppointment(patient, best);
        return ResponseEntity.ok(appt);
    }

    // New endpoints for appointments management
    @GetMapping("/appointments")
    public List<Appointment> listAppointments() {
        return appointmentService.getAllAppointments();
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable int id) {
        boolean ok = appointmentService.deleteAppointment(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // Basic patient listing and deletion (derived from appointments)
    @GetMapping("/patients")
    public List<Patient> listPatients() {
        return appointmentService.getAllPatients();
    }

    @DeleteMapping("/patients/{name}")
    public ResponseEntity<Void> deletePatientAppointments(@PathVariable String name) {
        int deleted = appointmentService.deleteAppointmentsByPatientName(name);
        if (deleted == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // Simple admin endpoints for hospitals
    @PostMapping("/hospitals")
    public ResponseEntity<Hospital> addHospital(@RequestBody Hospital h) {
        // Simple validation
        if (h == null || h.getName() == null) return ResponseEntity.badRequest().build();
        Hospital saved = hospitalService.addHospital(h);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/hospitals/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable int id) {
        boolean ok = hospitalService.deleteHospitalById(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}