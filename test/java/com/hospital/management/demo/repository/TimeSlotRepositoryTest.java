package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.*;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TimeSlotRepositoryTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Doctor testDoctor;
    private TimeSlot testTimeSlot;

    @BeforeEach
    void setUp() {
        User doctorUser = User.builder()
                .email("doctor@example.com")
                .password("password")
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();
        doctorUser = userRepository.save(doctorUser);

        Hospital testHospital = Hospital.builder()
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        testHospital = hospitalRepository.save(testHospital);

        Department testDepartment = Department.builder()
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

        testTimeSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isAvailable(true)
                .build();
        timeSlotRepository.save(testTimeSlot);
    }

    @Test
    void testFindByDoctorId() {
        // Cover findByDoctorId method
        List<TimeSlot> timeSlots = timeSlotRepository.findByDoctorId(testDoctor.getId());

        assertFalse(timeSlots.isEmpty());
    }

    @Test
    void testFindByDoctorIdAndDayOfWeek() {
        // Cover findByDoctorIdAndDayOfWeek method
        List<TimeSlot> timeSlots = timeSlotRepository.findByDoctorIdAndDayOfWeek(
                testDoctor.getId(), DayOfWeek.MONDAY);

        assertFalse(timeSlots.isEmpty());
    }

    @Test
    void testFindByDoctorIdAndIsAvailableTrue() {
        // Cover findByDoctorIdAndIsAvailableTrue method
        List<TimeSlot> timeSlots = timeSlotRepository.findByDoctorIdAndIsAvailableTrue(testDoctor.getId());

        assertFalse(timeSlots.isEmpty());
    }
}

