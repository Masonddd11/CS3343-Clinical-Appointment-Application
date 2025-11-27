package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Appointment;
import com.hospital.management.demo.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByHospitalId(Long hospitalId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status);
}

