package com.hospital.management.demo.config;

import com.hospital.management.demo.model.entity.Appointment;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.AppointmentRepository;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import com.hospital.management.demo.repository.PatientRepository;
import com.hospital.management.demo.repository.SymptomRepository;
import com.hospital.management.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SymptomRepository symptomRepository;
    private final AppointmentRepository appointmentRepository;

    @Value("${admin.default.email:admin@hospital.com}")
    private String defaultAdminEmail;

    @Value("${admin.default.password:admin123}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeDemoData();
    }

    private void initializeAdminUser() {
        if (userRepository.countByRole(UserRole.ADMIN) > 0) {
            log.info("Admin user already exists. Skipping default admin creation.");
            return;
        }

        log.info("No admin user found. Creating default admin user...");

        User adminUser = User.builder()
                .email(defaultAdminEmail)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(adminUser);
        log.info("Default admin user created successfully!");
        log.info("Email: {}", defaultAdminEmail);
        log.info("Password: {}", defaultAdminPassword);
        log.warn("Please change the default admin password after first login!");
    }

    private void initializeDemoData() {
        Hospital queenElizabeth = createHospitalIfNotExists(
                "Queen Elizabeth Hospital",
                "30 Gascoigne Rd, Jordan",
                22.3071,
                114.1722,
                "Kowloon",
                2000,
                0.4
        );

        Hospital princessMargaret = createHospitalIfNotExists(
                "Princess Margaret Hospital",
                "2-10 Princess Margaret Hospital Rd, Lai Chi Kok",
                22.3345,
                114.1373,
                "Kowloon",
                1500,
                0.6
        );

        Department urology = createDepartmentIfNotExists("Urology", "URO", "Kidney and urinary system treatment");
        Department cardiology = createDepartmentIfNotExists("Cardiology", "CAR", "Heart related care");

        assignDepartmentToHospital(queenElizabeth, urology);
        assignDepartmentToHospital(queenElizabeth, cardiology);
        assignDepartmentToHospital(princessMargaret, urology);

        Doctor doctorWong = createDoctorIfNotExists(
                "doctor@hospital.com",
                "doctor123",
                "Alice",
                "Wong",
                "12345678",
                "Urology",
                "MBBS, FRCS",
                "Specialist in kidney care",
                queenElizabeth,
                urology
        );

        createDoctorIfNotExists(
                "cardio@hospital.com",
                "doctor123",
                "Ben",
                "Lee",
                "87654321",
                "Cardiology",
                "MBBS, MRCP",
                "Experienced cardiologist",
                queenElizabeth,
                cardiology
        );

        Patient patientChan = createPatientIfNotExists(
                "patient@test.com",
                "patient123",
                "Bob",
                "Chan",
                "91234567",
                "Kowloon",
                22.33,
                114.17
        );

        createSymptomIfNotExists("kidney pain", urology, 1);
        createSymptomIfNotExists("heart pain", cardiology, 1);

        createSampleAppointmentIfAbsent(patientChan, doctorWong, queenElizabeth, urology);
    }

    private Hospital createHospitalIfNotExists(String name,
                                               String address,
                                               double latitude,
                                               double longitude,
                                               String district,
                                               int capacity,
                                               double intensity) {
        Optional<Hospital> existing = hospitalRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        Hospital hospital = Hospital.builder()
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .district(district)
                .capacity(capacity)
                .currentIntensity(intensity)
                .build();

        hospital = hospitalRepository.save(hospital);
        log.info("Created demo hospital: {}", name);
        return hospital;
    }

    private Department createDepartmentIfNotExists(String name, String code, String description) {
        return departmentRepository.findByCode(code)
                .orElseGet(() -> {
                    Department dept = Department.builder()
                            .name(name)
                            .code(code)
                            .description(description)
                            .build();
                    log.info("Created demo department: {}", name);
                    return departmentRepository.save(dept);
                });
    }

    private void assignDepartmentToHospital(Hospital hospital, Department department) {
        boolean exists = hospitalDepartmentRepository
                .findByHospitalIdAndDepartmentId(hospital.getId(), department.getId())
                .isPresent();

        if (exists) {
            return;
        }

        hospitalDepartmentRepository.save(
                com.hospital.management.demo.model.entity.HospitalDepartment.builder()
                        .hospital(hospital)
                        .department(department)
                        .isActive(true)
                        .build()
        );
        log.info("Assigned department {} to hospital {}", department.getName(), hospital.getName());
    }

    private Doctor createDoctorIfNotExists(String email,
                                           String password,
                                           String firstName,
                                           String lastName,
                                           String phone,
                                           String specialization,
                                           String qualifications,
                                           String bio,
                                           Hospital hospital,
                                           Department department) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return doctorRepository.findByUserId(existingUser.get().getId())
                    .orElseThrow();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();

        Doctor doctor = Doctor.builder()
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phone)
                .specialization(specialization)
                .qualifications(qualifications)
                .bio(bio)
                .build();

        doctor = doctorRepository.save(doctor);
        log.info("Created demo doctor: {} {}", firstName, lastName);
        return doctor;
    }

    private Patient createPatientIfNotExists(String email,
                                             String password,
                                             String firstName,
                                             String lastName,
                                             String phone,
                                             String district,
                                             Double latitude,
                                             Double longitude) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return patientRepository.findByUserId(existingUser.get().getId())
                    .orElseThrow();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();

        Patient patient = Patient.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phone)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address(district)
                .district(district)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        patient = patientRepository.save(patient);
        log.info("Created demo patient: {} {}", firstName, lastName);
        return patient;
    }

    private void createSymptomIfNotExists(String symptomText, Department department, int priority) {
        boolean exists = symptomRepository.findBySymptomContainingIgnoreCase(symptomText).stream()
                .anyMatch(symptom -> symptom.getSymptom().equalsIgnoreCase(symptomText));

        if (exists) {
            return;
        }

        Symptom symptom = Symptom.builder()
                .symptom(symptomText)
                .recommendedDepartment(department)
                .priority(priority)
                .build();

        symptomRepository.save(symptom);
        log.info("Created demo symptom: {}", symptomText);
    }

    private void createSampleAppointmentIfAbsent(Patient patient,
                                                 Doctor doctor,
                                                 Hospital hospital,
                                                 Department department) {
        LocalDate appointmentDate = LocalDate.now().plusDays(3);
        LocalTime appointmentTime = LocalTime.of(10, 0);

        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                doctor.getId(),
                appointmentDate,
                appointmentTime,
                com.hospital.management.demo.model.enums.AppointmentStatus.CANCELLED
        );

        if (exists) {
            return;
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .hospital(hospital)
                .department(department)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .reasonForVisit("Follow-up consultation")
                .symptoms("Kidney discomfort")
                .status(com.hospital.management.demo.model.enums.AppointmentStatus.CONFIRMED)
                .build();

        appointmentRepository.save(appointment);
        log.info("Created demo appointment on {} at {}", appointmentDate, appointmentTime);
    }
}

