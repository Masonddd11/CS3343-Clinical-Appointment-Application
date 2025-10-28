package clinicalappointment.model;

public class Appointment {
	private final Patient patient;
	private final Hospital hospital;

	public Appointment() {
		this.patient = null;
		this.hospital = null;
	}

	public Appointment(Patient patient, Hospital hospital) {
		this.patient = patient;
		this.hospital = hospital;
	}

	public Patient getPatient() { return patient; }
	public Hospital getHospital() { return hospital; }

	@Override
	public String toString() {
		return "Appointment: " + patient + " -> " + hospital;
	}
}
