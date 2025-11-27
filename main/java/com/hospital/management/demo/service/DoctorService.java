package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.DoctorRequest;
import com.hospital.management.demo.dto.DoctorResponse;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import com.hospital.management.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(
                request.getHospitalId(), request.getDepartmentId()).isEmpty()) {
            throw new RuntimeException("Department is not available at this hospital");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .hospital(hospital)
                .department(department)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .specialization(request.getSpecialization())
                .qualifications(request.getQualifications())
                .bio(request.getBio())
                .isAvailable(true)
                .build();

        doctor = doctorRepository.save(doctor);
        return mapToResponse(doctor);
    }

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapToResponse(doctor);
    }

    public List<DoctorResponse> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> getDoctorsByHospitalAndDepartment(Long hospitalId, Long departmentId) {
        return doctorRepository.findByHospitalIdAndDepartmentId(hospitalId, departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> searchDoctors(String name, String specialization) {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .filter(doctor -> {
                    boolean nameMatch = name == null || name.isEmpty() ||
                            (doctor.getFirstName() + " " + doctor.getLastName()).toLowerCase().contains(name.toLowerCase());
                    boolean specMatch = specialization == null || specialization.isEmpty() ||
                            (doctor.getSpecialization() != null && doctor.getSpecialization().toLowerCase().contains(specialization.toLowerCase()));
                    return nameMatch && specMatch;
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .email(doctor.getUser().getEmail())
                .hospitalId(doctor.getHospital().getId())
                .hospitalName(doctor.getHospital().getName())
                .departmentId(doctor.getDepartment().getId())
                .departmentName(doctor.getDepartment().getName())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .phoneNumber(doctor.getPhoneNumber())
                .specialization(doctor.getSpecialization())
                .qualifications(doctor.getQualifications())
                .bio(doctor.getBio())
                .isAvailable(doctor.getIsAvailable())
                .build();
    }
}

