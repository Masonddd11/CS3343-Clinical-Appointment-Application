package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.DoctorRequest;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.HospitalDepartment;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import com.hospital.management.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    private DoctorService doctorService;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private HospitalRepository hospitalRepository;
    private DepartmentRepository departmentRepository;
    private HospitalDepartmentRepository hospitalDepartmentRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        userRepository = mock(UserRepository.class);
        hospitalRepository = mock(HospitalRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        hospitalDepartmentRepository = mock(HospitalDepartmentRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        doctorService = new DoctorService(
                doctorRepository,
                userRepository,
                hospitalRepository,
                departmentRepository,
                hospitalDepartmentRepository,
                passwordEncoder);
    }

    @Test
    void testCreateDoctor_EmailExists() {
        // Branch: userRepository.existsByEmail returns true
        DoctorRequest request = new DoctorRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setFirstName("Test");
        request.setLastName("Doctor");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testCreateDoctor_HospitalNotFound() {
        // Branch: hospitalRepository.findById returns empty
        DoctorRequest request = new DoctorRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setFirstName("Test");
        request.setLastName("Doctor");

        when(userRepository.existsByEmail("doctor@example.com")).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertEquals("Hospital not found", exception.getMessage());
    }

    @Test
    void testCreateDoctor_DepartmentNotFound() {
        // Branch: departmentRepository.findById returns empty
        DoctorRequest request = new DoctorRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setFirstName("Test");
        request.setLastName("Doctor");

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        when(userRepository.existsByEmail("doctor@example.com")).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void testCreateDoctor_DepartmentNotAvailable() {
        // Branch: hospitalDepartmentRepository.findByHospitalIdAndDepartmentId returns
        // empty
        DoctorRequest request = new DoctorRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setFirstName("Test");
        request.setLastName("Doctor");

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(userRepository.existsByEmail("doctor@example.com")).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertEquals("Department is not available at this hospital", exception.getMessage());
    }

    @Test
    void testCreateDoctor_Success() {
        // Branch: All validations pass
        DoctorRequest request = new DoctorRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setFirstName("Test");
        request.setLastName("Doctor");

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(userRepository.existsByEmail("doctor@example.com")).thenReturn(false);
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.of(hospitalDepartment));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .password("encodedPassword")
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Doctor savedDoctor = Doctor.builder()
                .id(1L)
                .user(savedUser)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        var response = doctorService.createDoctor(request);

        assertNotNull(response);
        assertEquals("doctor@example.com", response.getEmail());
    }

    @Test
    void testGetDoctorById_Exists() {
        // Branch: Doctor exists
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        var response = doctorService.getDoctorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("doctor@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("Doctor", response.getLastName());
    }

    @Test
    void testGetDoctorById_NotExists() {
        // Branch: Doctor does not exist
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            doctorService.getDoctorById(1L);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    void testSearchDoctors_NameNull() {
        // Branch: name == null
        when(doctorRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var doctors = doctorService.searchDoctors(null, "Cardiology");

        assertNotNull(doctors);
    }

    @Test
    void testSearchDoctors_SpecializationNull() {
        // Branch: specialization == null
        when(doctorRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var doctors = doctorService.searchDoctors("Test", null);

        assertNotNull(doctors);
    }

    @Test
    void testGetAllDoctors() {
        // Cover getAllDoctors method
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.getAllDoctors();

        assertNotNull(doctors);
        assertEquals(1, doctors.size());
        assertEquals("Test", doctors.get(0).getFirstName());
    }

    @Test
    void testGetDoctorsByHospital() {
        // Cover getDoctorsByHospital method
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.findByHospitalId(1L)).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.getDoctorsByHospital(1L);

        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    void testGetDoctorsByDepartment() {
        // Cover getDoctorsByDepartment method
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.findByDepartmentId(1L)).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.getDoctorsByDepartment(1L);

        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    void testGetDoctorsByHospitalAndDepartment() {
        // Cover getDoctorsByHospitalAndDepartment method
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        when(doctorRepository.findByHospitalIdAndDepartmentId(1L, 1L)).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.getDoctorsByHospitalAndDepartment(1L, 1L);

        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    void testSearchDoctors_WithResults() {
        // Cover searchDoctors with matching results
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("Test", "Cardiology");

        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    void testSearchDoctors_NameEmpty() {
        // Branch: name.isEmpty() - 行111
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("", "Cardiology");

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // name为空时应该匹配所有
    }

    @Test
    void testSearchDoctors_SpecializationEmpty() {
        // Branch: specialization.isEmpty() - 行113
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("Test", "");

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // specialization为空时应该匹配所有
    }

    @Test
    void testSearchDoctors_SpecializationNullInDoctor() {
        // Branch: doctor.getSpecialization() == null - 行114
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization(null) // specialization为null
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("Test", "Cardiology");

        assertNotNull(doctors);
        assertEquals(0, doctors.size()); // specialization为null且搜索条件不为空，应该不匹配
    }

    @Test
    void testSearchDoctors_NameNotMatching() {
        // Branch: name不为null且不为空，但不匹配 - 行112
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("John", "Cardiology");

        assertNotNull(doctors);
        assertEquals(0, doctors.size()); // 名称不匹配
    }

    @Test
    void testSearchDoctors_SpecializationNotMatching() {
        // Branch: specialization不为null且不为空，但不匹配 - 行114
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("Test", "Neurology");

        assertNotNull(doctors);
        assertEquals(0, doctors.size()); // specialization不匹配
    }

    @Test
    void testSearchDoctors_BothNull() {
        // Branch: name == null && specialization == null - 行111, 113
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors(null, null);

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // 两者都为null时应该匹配所有
    }

    @Test
    void testSearchDoctors_BothEmpty() {
        // Branch: name.isEmpty() && specialization.isEmpty() - 行111, 113
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("", "");

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // 两者都为空时应该匹配所有
    }

    @Test
    void testSearchDoctors_PartialNameMatch() {
        // Branch: name匹配部分内容 - 行112
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("est", "Cardiology");

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // 部分名称匹配
    }

    @Test
    void testSearchDoctors_PartialSpecializationMatch() {
        // Branch: specialization匹配部分内容 - 行114
        User user = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName("Test")
                .lastName("Doctor")
                .specialization("Cardiology")
                .isAvailable(true)
                .build();
        when(doctorRepository.findAll()).thenReturn(java.util.List.of(doctor));

        var doctors = doctorService.searchDoctors("Test", "Card");

        assertNotNull(doctors);
        assertEquals(1, doctors.size()); // 部分specialization匹配
    }
}
