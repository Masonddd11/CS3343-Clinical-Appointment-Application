package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDoctorId(Long doctorId);
    List<TimeSlot> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
    List<TimeSlot> findByDoctorIdAndIsAvailableTrue(Long doctorId);
}

