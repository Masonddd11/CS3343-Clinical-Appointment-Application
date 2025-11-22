package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.*;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingRecommendationService {

    private final SymptomService symptomService;
    private final HospitalPathfinderService hospitalPathfinderService;
    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public SymptomAnalysisResponse analyzeSymptomsForDepartment(String symptoms) {
        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom(symptoms);
        
        if (result.isEmpty()) {
            return SymptomAnalysisResponse.builder()
                    .message("No matching department found for the given symptoms. Please select a department manually.")
                    .build();
        }

        SymptomService.SymptomMatchResult match = result.get();
        Department dept = match.getDepartment();
        
        return SymptomAnalysisResponse.builder()
                .departmentId(dept.getId())
                .departmentName(dept.getName())
                .departmentCode(dept.getCode())
                .confidenceScore(match.getConfidenceScore())
                .matchedKeywords(match.getMatchedKeywords())
                .message(String.format("Recommended department: %s (confidence: %.0f%%)", 
                        dept.getName(), match.getConfidenceScore() * 100))
                .build();
    }

    public List<HospitalRecommendationResponse> recommendHospitalsForPatient(Long patientId, Long departmentId, Integer maxResults) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getLatitude() == null || patient.getLongitude() == null) {
            throw new RuntimeException("Patient location not set. Cannot provide hospital recommendations.");
        }

        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setLatitude(patient.getLatitude());
        request.setLongitude(patient.getLongitude());
        request.setDepartmentId(departmentId);
        request.setMaxResults(maxResults != null ? maxResults : 5);

        return hospitalPathfinderService.recommendHospitals(request);
    }

    @Transactional
    public SmartAppointmentResponse processSmartBooking(Long patientId, SmartAppointmentRequest request) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        SymptomAnalysisResponse departmentRecommendation = analyzeSymptomsForDepartment(request.getSymptoms());
        List<HospitalRecommendationResponse> hospitalRecommendations = new ArrayList<>();
        AppointmentResponse appointment = null;
        String message = "";

        Long departmentId = request.getDepartmentId();
        if (departmentId == null && departmentRecommendation.getDepartmentId() != null) {
            departmentId = departmentRecommendation.getDepartmentId();
            message = "Department auto-selected based on symptoms: " + departmentRecommendation.getDepartmentName() + ". ";
        } else if (departmentId == null) {
            throw new RuntimeException("Could not determine department from symptoms. Please specify departmentId manually.");
        }

        if (request.getHospitalId() == null) {
            hospitalRecommendations = recommendHospitalsForPatient(
                    patientId,
                    departmentId,
                    request.getMaxHospitalRecommendations()
            );

            if (hospitalRecommendations.isEmpty()) {
                throw new RuntimeException("No hospitals found for the selected department");
            }

            message += "Hospital recommendations provided. Please select a hospital and doctor to complete booking.";
        } else {
            if (request.getDoctorId() == null) {
                List<Doctor> availableDoctors = doctorRepository.findByHospitalIdAndDepartmentId(
                        request.getHospitalId(),
                        departmentId
                );

                if (availableDoctors.isEmpty()) {
                    throw new RuntimeException("No doctors available at the selected hospital and department");
                }

                message += "Please select a doctor to complete booking. Available doctors: " +
                        availableDoctors.size();
            } else {
                AppointmentRequest appointmentRequest = buildAppointmentRequest(request, departmentId);
                Double pathfindingScore = calculatePathfindingScore(patientId, request.getHospitalId(), departmentId);

                appointment = appointmentService.bookAppointmentWithPathfindingScore(
                        patientId,
                        appointmentRequest,
                        pathfindingScore
                );
                message = "Appointment booked successfully!";
            }
        }

        return SmartAppointmentResponse.builder()
                .appointment(appointment)
                .departmentRecommendation(departmentRecommendation)
                .hospitalRecommendations(hospitalRecommendations)
                .message(message)
                .build();
    }

    private AppointmentRequest buildAppointmentRequest(SmartAppointmentRequest smartRequest, Long departmentId) {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(smartRequest.getDoctorId());
        request.setHospitalId(smartRequest.getHospitalId());
        request.setDepartmentId(departmentId);
        request.setAppointmentDate(smartRequest.getAppointmentDate());
        request.setAppointmentTime(smartRequest.getAppointmentTime());
        request.setReasonForVisit(smartRequest.getReasonForVisit());
        request.setSymptoms(smartRequest.getSymptoms());
        return request;
    }

    private Double calculatePathfindingScore(Long patientId, Long hospitalId, Long departmentId) {
        try {
            List<HospitalRecommendationResponse> recommendations = recommendHospitalsForPatient(
                    patientId,
                    departmentId,
                    10
            );
            return recommendations.stream()
                    .filter(r -> r.getHospitalId().equals(hospitalId))
                    .findFirst()
                    .map(HospitalRecommendationResponse::getScore)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

