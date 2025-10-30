package clinicalappointment.model;

import java.time.Instant;

public class Appointment {
    private final int id;
    private final Patient patient;
    private final Hospital hospital;
    private final Instant createdAt;
    private final String status;

    // Default constructor
    public Appointment() {
        this.id = 0;
        this.patient = null;
        this.hospital = null;
        this.createdAt = null;
        this.status = "";
    }

    public Appointment(int id, Patient patient, Hospital hospital, Instant createdAt, String status) {
        this.id = id;
        this.patient = patient;
        this.hospital = hospital;
        this.createdAt = createdAt;
        this.status = status == null ? "BOOKED" : status;
    }

    public Appointment(int id, Patient patient, Hospital hospital) {
        this(id, patient, hospital, Instant.now(), "BOOKED");
    }

    public int getId() { return id; }
    public Patient getPatient() { return patient; }
    public Hospital getHospital() { return hospital; }
    public Instant getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "Appointment: " + patient + " -> " + hospital;
    }
}
