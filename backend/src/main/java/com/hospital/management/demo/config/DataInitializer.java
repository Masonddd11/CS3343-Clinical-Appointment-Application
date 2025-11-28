package com.hospital.management.demo.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.seed.HospitalSeedDto;
import com.hospital.management.demo.integration.district.DistrictMapper;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ObjectMapper objectMapper;
    private final DistrictMapper districtMapper;

    private static final String HOSPITAL_SEED_FILE = "hospital.Json";
    private static final double AE_INTENSITY = 0.45;
    private static final double NON_AE_INTENSITY = 0.25;
    private static final int AE_CAPACITY = 1200;
    private static final int NON_AE_CAPACITY = 600;

    private static final List<DepartmentSeed> DEPARTMENT_SEEDS = List.of(
            new DepartmentSeed("Cardiology", "CAR", "Heart related care"),
            new DepartmentSeed("Urology", "URO", "Kidney and urinary system treatment"),
            new DepartmentSeed("Neurology", "NEU", "Brain and nervous system disorders"),
            new DepartmentSeed("Orthopedics", "ORT", "Bone and joint injuries"),
            new DepartmentSeed("Dermatology", "DER", "Skin, hair, and nail conditions"),
            new DepartmentSeed("Gastroenterology", "GAS", "Digestive system issues"),
            new DepartmentSeed("Ophthalmology", "OPH", "Eye and vision care"),
            new DepartmentSeed("Otolaryngology", "ENT", "Ear, nose, and throat treatment"),
            new DepartmentSeed("Pulmonology", "PUL", "Lung and respiratory care"),
            new DepartmentSeed("Endocrinology", "END", "Hormone and metabolic disorders")
    );

    private static final List<DoctorSeed> DOCTOR_SEEDS = List.of(
            new DoctorSeed("doctor@hospital.com", "doctor123", "Alice", "Wong", "12345678", "Urology", "MBBS, FRCS", "Specialist in kidney care", "Queen Elizabeth Hospital", "URO"),
            new DoctorSeed("cardio@hospital.com", "doctor123", "Ben", "Lee", "87654321", "Cardiology", "MBBS, MRCP", "Experienced cardiologist", "Queen Elizabeth Hospital", "CAR"),
            new DoctorSeed("neuro@hospital.com", "doctor123", "Carol", "Ho", "61234567", "Neurology", "MBBS, FRCP", "Brain and nervous system specialist", "Queen Elizabeth Hospital", "NEU"),
            new DoctorSeed("ortho@hospital.com", "doctor123", "David", "Lam", "69876543", "Orthopedics", "MBBS, FRCS(Ortho)", "Bone and joint surgeon", "Princess Margaret Hospital", "ORT"),
            new DoctorSeed("derma@hospital.com", "doctor123", "Eva", "Ng", "93456781", "Dermatology", "MBBS, MRCP(Derm)", "Skin specialist", "Queen Elizabeth Hospital", "DER"),
            // Demo doctors for Queen Elizabeth Hospital covering all seeded departments
            new DoctorSeed("qe-cardio@hospital.com", "doctor123", "Lydia", "Chung", "93110001", "Cardiology", "MBBS, MRCP", "Cardiologist at Kwong Wah", "Queen Elizabeth Hospital", "CAR"),
            new DoctorSeed("qe-uro@hospital.com", "doctor123", "Michael", "Tam", "93110002", "Urology", "MBBS, FRCS", "Urology specialist at Kwong Wah", "Queen Elizabeth Hospital", "URO"),
            new DoctorSeed("qe-neuro@hospital.com", "doctor123", "Siu", "Chan", "93110003", "Neurology", "MBBS, FRCP", "Neurologist at Kwong Wah", "Queen Elizabeth Hospital", "NEU"),
            new DoctorSeed("qe-ortho@hospital.com", "doctor123", "Grace", "Wong", "93110004", "Orthopedics", "MBBS, FRCS(Ortho)", "Orthopedic surgeon at Kwong Wah", "Queen Elizabeth Hospital", "ORT"),
            new DoctorSeed("qe-derm@hospital.com", "doctor123", "Victor", "Ho", "93110005", "Dermatology", "MBBS, MRCP(Derm)", "Dermatologist at Kwong Wah", "Queen Elizabeth Hospital", "DER"),
            new DoctorSeed("qe-gast@hospital.com", "doctor123", "Anita", "Leung", "93110006", "Gastroenterology", "MBBS, FRCP", "Gastroenterologist at Kwong Wah", "Queen Elizabeth Hospital", "GAS"),
            new DoctorSeed("qe-oph@hospital.com", "doctor123", "Thomas", "Yip", "93110007", "Ophthalmology", "MBBS, FRCOphth", "Ophthalmologist at Kwong Wah", "Queen Elizabeth Hospital", "OPH"),
            new DoctorSeed("qe-ent@hospital.com", "doctor123", "Nancy", "Kwan", "93110008", "Otolaryngology", "MBBS, FRCS(ENT)", "ENT specialist at Kwong Wah", "Queen Elizabeth Hospital", "ENT"),
            new DoctorSeed("qe-pul@hospital.com", "doctor123", "Eric", "Choi", "93110009", "Pulmonology", "MBBS, MRCP", "Pulmonologist at Kwong Wah", "Queen Elizabeth Hospital", "PUL"),
            new DoctorSeed("qe-end@hospital.com", "doctor123", "Helen", "Cheung", "93110010", "Endocrinology", "MBBS, FRCP", "Endocrinologist at Kwong Wah", "Queen Elizabeth Hospital", "END")
    );

    private static final List<SymptomSeed> SYMPTOM_SEEDS = List.of(
            new SymptomSeed("kidney pain", "URO", 1, List.of("renal pain", "flank pain", "kidney discomfort", "urinary pain")),
            new SymptomSeed("heart pain", "CAR", 1, List.of("chest pain", "cardiac pain", "chest tightness", "palpitations")),
            new SymptomSeed("migraine", "NEU", 2, List.of("headache", "throbbing head", "light sensitivity", "aura")),
            new SymptomSeed("back injury", "ORT", 2, List.of("fracture", "sprain", "joint pain", "bone pain")),
            new SymptomSeed("skin rash", "DER", 2, List.of("itchy skin", "red patches", "eczema", "dermatitis")),
            new SymptomSeed("stomach ache", "GAS", 2, List.of("abdominal pain", "bloating", "acid reflux", "indigestion")),
            new SymptomSeed("blurred vision", "OPH", 2, List.of("vision loss", "eye pain", "double vision", "dry eyes")),
            new SymptomSeed("sore throat", "ENT", 3, List.of("ear pain", "blocked nose", "sinus", "hoarseness")),
            new SymptomSeed("shortness of breath", "PUL", 1, List.of("wheezing", "cough", "respiratory distress", "asthma")),
            new SymptomSeed("excessive thirst", "END", 2, List.of("fatigue", "weight change", "blood sugar", "hormone"))
    );

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
        List<HospitalSeedDto> hospitalSeedData = loadHospitalSeedData();
        if (hospitalSeedData.isEmpty()) {
            log.error("Hospital seed data not available. Skipping hospital initialization.");
            return;
        }

        hospitalSeedData.forEach(this::upsertHospitalFromSeed);
        log.info("Hospital dataset synchronized from {} entries.", hospitalSeedData.size());

        Map<String, Department> departmentByCode = DEPARTMENT_SEEDS.stream()
                .collect(Collectors.toMap(DepartmentSeed::code, seed -> createDepartmentIfNotExists(seed.name(), seed.code(), seed.description())));

        Hospital queenElizabeth = findHospitalByNameOrThrow("Queen Elizabeth Hospital");
        Hospital princessMargaret = findHospitalByNameOrThrow("Princess Margaret Hospital");

        assignDefaultDepartments(List.of(queenElizabeth, princessMargaret), departmentByCode);

        List<Doctor> seededDoctors = DOCTOR_SEEDS.stream()
                .map(seed -> createDoctorIfNotExists(
                        seed.email(),
                        seed.password(),
                        seed.firstName(),
                        seed.lastName(),
                        seed.phone(),
                        seed.specialization(),
                        seed.qualifications(),
                        seed.bio(),
                        findHospitalByNameOrThrow(seed.hospitalName()),
                        departmentByCode.get(seed.departmentCode())
                ))
                .toList();

        Doctor doctorWong = seededDoctors.stream()
                .filter(doc -> doc.getUser().getEmail().equals("doctor@hospital.com"))
                .findFirst()
                .orElseThrow();

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

        SYMPTOM_SEEDS.forEach(seed -> createSymptomIfNotExists(
                seed.name(),
                departmentByCode.get(seed.departmentCode()),
                seed.priority(),
                seed.keywords()
        ));

        createSampleAppointmentIfAbsent(patientChan, doctorWong, queenElizabeth, departmentByCode.get("URO"));
    }

    private List<HospitalSeedDto> loadHospitalSeedData() {
        ClassPathResource resource = new ClassPathResource(HOSPITAL_SEED_FILE);
        if (!resource.exists()) {
            log.error("Hospital seed file {} not found on classpath.", HOSPITAL_SEED_FILE);
            return Collections.emptyList();
        }
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException ex) {
            log.error("Failed to parse hospital seed JSON.", ex);
            return Collections.emptyList();
        }
    }

    private void upsertHospitalFromSeed(HospitalSeedDto seed) {
        if (seed == null || seed.institutionEng() == null) {
            log.warn("Encountered hospital seed entry with missing institution name. Skipping entry: {}", seed);
            return;
        }
        if (seed.latitude() == null || seed.longitude() == null) {
            log.warn("Hospital entry {} missing coordinates. Skipping.", seed.institutionEng());
            return;
        }

        Hospital hospital = hospitalRepository.findByName(seed.institutionEng())
                .orElseGet(() -> Hospital.builder().name(seed.institutionEng()).build());

        hospital.setAddress(seed.addressEng());
        hospital.setLatitude(seed.latitude());
        hospital.setLongitude(seed.longitude());
        hospital.setDistrict(districtMapper.map(seed.institutionEng(), seed.clusterEng()));
        hospital.setCapacity(determineCapacity(seed));
        hospital.setCurrentIntensity(seed.hasAccidentAndEmergency() ? AE_INTENSITY : NON_AE_INTENSITY);
        hospital.setHasAccidentAndEmergency(seed.hasAccidentAndEmergency());

        hospitalRepository.save(hospital);
        log.debug("Upserted hospital entry: {}", seed.institutionEng());
    }

    private int determineCapacity(HospitalSeedDto seed) {
        return seed.hasAccidentAndEmergency() ? AE_CAPACITY : NON_AE_CAPACITY;
    }

    private Hospital findHospitalByNameOrThrow(String name) {
        return hospitalRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Required hospital not found in seed data: " + name));
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

    private void createSymptomIfNotExists(String symptomText, Department department, int priority, List<String> keywords) {
        boolean exists = symptomRepository.findBySymptomContainingIgnoreCase(symptomText).stream()
                .anyMatch(symptom -> symptom.getSymptom().equalsIgnoreCase(symptomText));

        if (exists) {
            return;
        }

        Symptom symptom = Symptom.builder()
                .symptom(symptomText)
                .recommendedDepartment(department)
                .priority(priority)
                .keywords(keywords)
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

    private void assignDefaultDepartments(List<Hospital> hospitals, Map<String, Department> departments) {
        hospitals.forEach(hospital -> departments.values().forEach(dept -> {
            if ((hospital.getName().contains("Queen") && !List.of("ENT", "PUL", "END").contains(dept.getCode())) ||
                (hospital.getName().contains("Princess") && List.of("URO", "ORT", "DER", "OPH").contains(dept.getCode()))) {
                assignDepartmentToHospital(hospital, dept);
            }
        }));
    }

    private record DepartmentSeed(String name, String code, String description) {}
    private record DoctorSeed(String email, String password, String firstName, String lastName, String phone,
                              String specialization, String qualifications, String bio,
                              String hospitalName, String departmentCode) {}
    private record SymptomSeed(String name, String departmentCode, int priority, List<String> keywords) {}
}
