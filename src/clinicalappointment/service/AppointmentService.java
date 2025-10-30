package clinicalappointment.service;

import clinicalappointment.model.Appointment;
import clinicalappointment.model.Patient;
import clinicalappointment.model.Hospital;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final Map<Integer, Appointment> appointments = new LinkedHashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(1);
    private final AtomicInteger patientIdGen = new AtomicInteger(1);

    public AppointmentService() {
        // empty
    }

    public Appointment createAppointment(Patient patient, Hospital hospital) {
        // assign patient id if missing
        if (patient.getId() == null || patient.getId().isEmpty()) {
            patient.setId("P" + patientIdGen.getAndIncrement());
        }
        int id = idGen.getAndIncrement();
        Appointment a = new Appointment(id, patient, hospital, Instant.now(), "BOOKED");
        appointments.put(id, a);
        return a;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    public boolean deleteAppointment(int id) {
        return appointments.remove(id) != null;
    }

    public List<Patient> getAllPatients() {
        return appointments.values().stream()
                .map(Appointment::getPatient)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int deleteAppointmentsByPatientName(String name) {
        List<Integer> toRemove = appointments.values().stream()
                .filter(a -> a.getPatient() != null && name.equals(a.getPatient().getName()))
                .map(Appointment::getId)
                .collect(Collectors.toList());
        toRemove.forEach(appointments::remove);
        return toRemove.size();
    }
}
