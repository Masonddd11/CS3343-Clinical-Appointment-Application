package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.AppointmentRequest;
import com.hospital.management.demo.dto.RescheduleAppointmentRequest;
import com.hospital.management.demo.model.entity.*;
import com.hospital.management.demo.model.enums.AppointmentStatus;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private HospitalRepository hospitalRepository;
    private DepartmentRepository departmentRepository;

    private Patient testPatient;
    private Doctor testDoctor;
    private Hospital testHospital;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        hospitalRepository = mock(HospitalRepository.class);
        departmentRepository = mock(DepartmentRepository.class);

        appointmentService = new AppointmentService(
                appointmentRepository,
                patientRepository,
                doctorRepository,
                hospitalRepository,
                departmentRepository
        );

        User patientUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .role(UserRole.PATIENT)
                .build();
        testPatient = Patient.builder()
                .id(1L)
                .user(patientUser)
                .firstName("Test")
                .lastName("Patient")
                .build();

        User doctorUser = User.builder()
                .id(2L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        testHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        testDepartment = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        testDoctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .hospital(testHospital)
                .department(testDepartment)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
    }

    @Test
    void testBookAppointment_PatientNotFound() {
        // Branch: patientRepository.findById returns empty
        AppointmentRequest request = new AppointmentRequest();
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
    }

    @Test
    void testBookAppointment_DoctorNotFound() {
        // Branch: doctorRepository.findById returns empty - 行42
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    void testBookAppointment_HospitalNotFound() {
        // Branch: hospitalRepository.findById returns empty - 行45
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Hospital not found", exception.getMessage());
    }

    @Test
    void testBookAppointment_DepartmentNotFound() {
        // Branch: departmentRepository.findById returns empty - 行48
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void testBookAppointment_HospitalClosedEpidemic() {
        // Branch: hospital.getOperationalStatus() == CLOSED_EPIDEMIC - 行50-53
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        Hospital closedHospital = Hospital.builder()
                .id(1L)
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(closedHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Hospital is currently closed", exception.getMessage());
    }

    @Test
    void testBookAppointment_HospitalClosedOther() {
        // Branch: hospital.getOperationalStatus() == CLOSED_OTHER - 行50-53
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        Hospital closedHospital = Hospital.builder()
                .id(1L)
                .operationalStatus(OperationalStatus.CLOSED_OTHER)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(closedHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Hospital is currently closed", exception.getMessage());
    }

    @Test
    void testBookAppointment_DoctorNotAtHospital() {
        // Branch: !doctor.getHospital().getId().equals(hospital.getId())
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(2L);
        request.setDepartmentId(1L);

        Hospital differentHospital = Hospital.builder()
                .id(2L)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(2L)).thenReturn(Optional.of(differentHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
    }

    @Test
    void testBookAppointment_DoctorNotAvailable() {
        // Branch: !doctor.getIsAvailable()
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(2));
        request.setAppointmentTime(LocalTime.of(10, 0));

        Doctor unavailableDoctor = Doctor.builder()
                .id(1L)
                .hospital(testHospital)
                .department(testDepartment)
                .isAvailable(false)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(unavailableDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
    }

    @Test
    void testBookAppointment_TooEarly() {
        // Branch: appointmentDateTime.isBefore(now.plusHours(24))
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setAppointmentDate(LocalDate.now());
        request.setAppointmentTime(LocalTime.now().plusHours(1));
        request.setReasonForVisit("Checkup");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
    }

    @Test
    void testBookAppointment_Success() {
        // Branch: All validations pass
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(2));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReasonForVisit("Checkup");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                anyLong(), any(), any(), any())).thenReturn(false);

        Appointment savedAppointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reasonForVisit(request.getReasonForVisit())
                .status(AppointmentStatus.CONFIRMED)
                .build();
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        var response = appointmentService.bookAppointment(1L, request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
    }

    @Test
    void testCancelAppointment_AlreadyCancelled() {
        // Branch: appointment.getStatus() == CANCELLED
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CANCELLED)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(1L, 1L);
        });
    }

    @Test
    void testRescheduleAppointment_WrongPatient() {
        // Branch: !appointment.getPatient().getId().equals(patientId)
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 2L, request);
        });
    }

    @Test
    void testGetAppointmentById_NotExists() {
        // Branch: Appointment does not exist
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.getAppointmentById(1L);
        });
    }

    @Test
    void testGetPatientAppointments() {
        // Cover getPatientAppointments method
        when(appointmentRepository.findByPatientId(1L)).thenReturn(java.util.Collections.emptyList());

        var appointments = appointmentService.getPatientAppointments(1L);

        assertNotNull(appointments);
    }

    @Test
    void testGetDoctorAppointments() {
        // Cover getDoctorAppointments method
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(java.util.Collections.emptyList());

        var appointments = appointmentService.getDoctorAppointments(1L);

        assertNotNull(appointments);
    }

    @Test
    void testBookAppointment_DoctorNotInDepartment() {
        // Branch: !doctor.getDepartment().getId().equals(department.getId()) - 行60
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(2L);
        request.setAppointmentDate(LocalDate.now().plusDays(2));
        request.setAppointmentTime(LocalTime.of(10, 0));

        Department differentDepartment = Department.builder()
                .id(2L)
                .name("Neurology")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(differentDepartment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Doctor does not belong to the specified department", exception.getMessage());
    }

    @Test
    void testBookAppointment_TooFarInFuture() {
        // Branch: appointmentDateTime.isAfter(now.plusMonths(3)) - 行75
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setAppointmentDate(LocalDate.now().plusMonths(4));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReasonForVisit("Checkup");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Appointments can only be booked up to 3 months in advance", exception.getMessage());
    }

    @Test
    void testBookAppointment_TimeSlotBooked() {
        // Branch: existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot returns true - 行80
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setHospitalId(1L);
        request.setDepartmentId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(2));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReasonForVisit("Checkup");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                eq(1L), eq(request.getAppointmentDate()), eq(request.getAppointmentTime()), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(1L, request);
        });
        assertEquals("Time slot is already booked", exception.getMessage());
    }

    @Test
    void testGetAppointmentById_Success() {
        // Branch: getAppointmentById成功返回 - 行114
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        var response = appointmentService.getAppointmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
    }

    @Test
    void testRescheduleAppointment_Success() {
        // Branch: 成功重新安排预约 - 行126-151
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        Appointment updatedAppointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                eq(1L), eq(request.getAppointmentDate()), eq(request.getAppointmentTime()), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        var response = appointmentService.rescheduleAppointment(1L, 1L, request);

        assertNotNull(response);
        assertEquals(request.getAppointmentDate(), response.getAppointmentDate());
        assertEquals(request.getAppointmentTime(), response.getAppointmentTime());
    }

    @Test
    void testRescheduleAppointment_Cancelled() {
        // Branch: appointment.getStatus() == CANCELLED - 行126-128
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CANCELLED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
        assertEquals("Cannot reschedule cancelled or completed appointment", exception.getMessage());
    }

    @Test
    void testRescheduleAppointment_Completed() {
        // Branch: appointment.getStatus() == COMPLETED - 行126-128
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.COMPLETED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
        assertEquals("Cannot reschedule cancelled or completed appointment", exception.getMessage());
    }

    @Test
    void testRescheduleAppointment_OldTimeTooSoon() {
        // Branch: oldAppointmentDateTime.isBefore(now.plusHours(24)) - 行134-136
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now())
                .appointmentTime(LocalTime.now().plusHours(12))
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
        assertEquals("Appointments can only be rescheduled at least 24 hours before the appointment time", exception.getMessage());
    }

    @Test
    void testRescheduleAppointment_NewTimeTooSoon() {
        // Branch: newAppointmentDateTime.isBefore(now.plusHours(24)) - 行138-140
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now());
        request.setAppointmentTime(LocalTime.now().plusHours(12));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
        assertEquals("New appointment time must be at least 24 hours in advance", exception.getMessage());
    }

    @Test
    void testRescheduleAppointment_NewTimeSlotBooked() {
        // Branch: 新时间段已被预约 - 行142-145
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                eq(1L), eq(request.getAppointmentDate()), eq(request.getAppointmentTime()), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
        assertEquals("New time slot is already booked", exception.getMessage());
    }

    @Test
    void testRescheduleAppointment_NotFound() {
        // Branch: appointmentRepository.findById returns empty - 行119-120
        RescheduleAppointmentRequest request = new RescheduleAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setAppointmentTime(LocalTime.of(14, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.rescheduleAppointment(1L, 1L, request);
        });
    }

    @Test
    void testCancelAppointment_Success() {
        // Branch: 成功取消预约 - 行167-180
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.cancelAppointment(1L, 1L);

        verify(appointmentRepository).save(argThat(apt -> 
            apt.getStatus() == AppointmentStatus.CANCELLED
        ));
    }

    @Test
    void testCancelAppointment_Completed() {
        // Branch: appointment.getStatus() == COMPLETED - 行167-169
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.COMPLETED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(1L, 1L);
        });
        assertEquals("Cannot cancel completed appointment", exception.getMessage());
    }

    @Test
    void testCancelAppointment_TooSoon() {
        // Branch: appointmentDateTime.isBefore(now.plusHours(24)) - 行174-176
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now())
                .appointmentTime(LocalTime.now().plusHours(12))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(1L, 1L);
        });
        assertEquals("Appointments can only be cancelled at least 24 hours before the appointment time", exception.getMessage());
    }

    @Test
    void testCancelAppointment_NotFound() {
        // Branch: appointmentRepository.findById returns empty - 行156-157
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(1L, 1L);
        });
    }

    @Test
    void testCancelAppointment_WrongPatient() {
        // Branch: !appointment.getPatient().getId().equals(patientId) - 行159-161
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.now().plusDays(3))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(1L, 2L);
        });
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void testMarkAppointmentCompleted_Success() {
        // Branch: 成功标记完成 - 行183-193
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Appointment completedAppointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .hospital(testHospital)
                .department(testDepartment)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(completedAppointment);

        var response = appointmentService.markAppointmentCompleted(1L, 1L);

        assertNotNull(response);
        assertEquals(AppointmentStatus.COMPLETED, response.getStatus());
    }

    @Test
    void testMarkAppointmentCompleted_WrongDoctor() {
        // Branch: !appointment.getDoctor().getId().equals(doctorId) - 行187-189
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.markAppointmentCompleted(1L, 2L);
        });
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void testMarkAppointmentCompleted_NotFound() {
        // Branch: appointmentRepository.findById returns empty - 行184-185
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.markAppointmentCompleted(1L, 1L);
        });
    }
}

