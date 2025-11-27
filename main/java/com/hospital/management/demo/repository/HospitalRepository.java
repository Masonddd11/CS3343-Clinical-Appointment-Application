package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByOperationalStatus(OperationalStatus status);
    List<Hospital> findByOperationalStatusNot(OperationalStatus status);
    List<Hospital> findByDistrict(String district);
    Optional<Hospital> findByName(String name);
}

