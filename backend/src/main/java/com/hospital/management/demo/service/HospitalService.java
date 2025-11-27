package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.dto.HospitalResponse;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    @Transactional
    public HospitalResponse createHospital(HospitalRequest request) {
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .district(request.getDistrict())
                .capacity(request.getCapacity())
                .currentIntensity(request.getCurrentIntensity() != null ? request.getCurrentIntensity() : 0.0)
                .operationalStatus(request.getOperationalStatus() != null ? request.getOperationalStatus() : OperationalStatus.OPERATIONAL)
                .closureReason(request.getClosureReason())
                .hasAccidentAndEmergency(Boolean.TRUE.equals(request.getHasAccidentAndEmergency()))
                .build();

        hospital = hospitalRepository.save(hospital);
        return mapToResponse(hospital);
    }

    public List<HospitalResponse> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public HospitalResponse getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        return mapToResponse(hospital);
    }

    @Transactional
    public HospitalResponse updateHospital(Long id, HospitalRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setLatitude(request.getLatitude());
        hospital.setLongitude(request.getLongitude());
        hospital.setDistrict(request.getDistrict());
        if (request.getCapacity() != null) hospital.setCapacity(request.getCapacity());
        if (request.getCurrentIntensity() != null) hospital.setCurrentIntensity(request.getCurrentIntensity());
        if (request.getOperationalStatus() != null) hospital.setOperationalStatus(request.getOperationalStatus());
        if (request.getClosureReason() != null) hospital.setClosureReason(request.getClosureReason());
        hospital.setHasAccidentAndEmergency(Boolean.TRUE.equals(request.getHasAccidentAndEmergency()));

        hospital = hospitalRepository.save(hospital);
        return mapToResponse(hospital);
    }

    private HospitalResponse mapToResponse(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .latitude(hospital.getLatitude())
                .longitude(hospital.getLongitude())
                .district(hospital.getDistrict())
                .capacity(hospital.getCapacity())
                .currentIntensity(hospital.getCurrentIntensity())
                .operationalStatus(hospital.getOperationalStatus())
                .closureReason(hospital.getClosureReason())
                .hasAccidentAndEmergency(hospital.getHasAccidentAndEmergency())
                .build();
    }
}
