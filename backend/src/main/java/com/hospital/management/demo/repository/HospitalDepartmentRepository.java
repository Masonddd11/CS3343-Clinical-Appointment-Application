package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.HospitalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalDepartmentRepository extends JpaRepository<HospitalDepartment, Long> {
    List<HospitalDepartment> findByHospitalId(Long hospitalId);
    List<HospitalDepartment> findByDepartmentId(Long departmentId);
    Optional<HospitalDepartment> findByHospitalIdAndDepartmentId(Long hospitalId, Long departmentId);
    List<HospitalDepartment> findByHospitalIdAndIsActiveTrue(Long hospitalId);
}

