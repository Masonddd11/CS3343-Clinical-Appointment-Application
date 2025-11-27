package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserEmail(String email);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByHospitalId(Long hospitalId);
    List<Doctor> findByDepartmentId(Long departmentId);
    List<Doctor> findByHospitalIdAndDepartmentId(Long hospitalId, Long departmentId);
    List<Doctor> findByIsAvailableTrue();
}

