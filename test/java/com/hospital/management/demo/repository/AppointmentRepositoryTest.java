package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.*;
import com.hospital.management.demo.model.enums.AppointmentStatus;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Patient testPatient;
    private Doctor testDoctor;
    private Hospital testHospital;
    private Department testDepartment;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        User patientUser = User.builder()
                .email("patient@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        patientUser = userRepository.save(patientUser);

        testPatient = Patient.builder()
                .user(patientUser)
                .firstName("Test")
                .lastName("Patient")
                .build();
        testPatient = patientRepository.save(testPatient);

        User doctorUser = User.builder()
                .email("doctor@example.com")
                .password("password")
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();
        doctorUser = userRepository.save(doctorUser);

        testHospital = Hospital.builder()
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        testHospital = hospitalRepository.save(testHospital);

        testDepartment = Department.builder()
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testDoctor = Doctor.builder()
                .user(doctorUser)
                .hospital(testHospital)
                .department(testDepartment)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        testAppointment = Appointment.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .reasonForVisit("Checkup")
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentRepository.save(testAppointment);
    }

    @Test
    void testFindByPatientId() {
        // Cover findByPatientId method
        List<Appointment> appointments = appointmentRepository.findByPatientId(testPatient.getId());

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testFindByDoctorId() {
        // Cover findByDoctorId method
        List<Appointment> appointments = appointmentRepository.findByDoctorId(testDoctor.getId());

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testFindByHospitalId() {
        // Cover findByHospitalId method
        List<Appointment> appointments = appointmentRepository.findByHospitalId(testHospital.getId());

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testFindByStatus() {
        // Cover findByStatus method
        List<Appointment> appointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING);

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testFindByPatientIdAndStatus() {
        // Cover findByPatientIdAndStatus method
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(
                testPatient.getId(), AppointmentStatus.PENDING);

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testFindByDoctorIdAndStatus() {
        // Cover findByDoctorIdAndStatus method
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndStatus(
                testDoctor.getId(), AppointmentStatus.PENDING);

        assertFalse(appointments.isEmpty());
    }

    @Test
    void testExistsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot_True() {
        // Branch: Appointment exists with matching criteria
        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                testDoctor.getId(),
                testAppointment.getAppointmentDate(),
                testAppointment.getAppointmentTime(),
                AppointmentStatus.CANCELLED);

        assertTrue(exists);
    }

    @Test
    void testExistsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot_False() {
        // Branch: Appointment does not exist with matching criteria
        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                testDoctor.getId(),
                LocalDate.now().plusDays(10),
                LocalTime.of(15, 0),
                AppointmentStatus.CANCELLED);

        assertFalse(exists);
    }
}

