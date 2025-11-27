package com.hospital.management.demo.service;

import com.hospital.management.demo.algorithm.WeightCalculator;
import com.hospital.management.demo.dto.HospitalRecommendationRequest;
import com.hospital.management.demo.dto.HospitalRecommendationResponse;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalPathfinderService {

    private final HospitalRepository hospitalRepository;
    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final WeightCalculator weightCalculator;

    public List<HospitalRecommendationResponse> recommendHospitals(HospitalRecommendationRequest request) {
        List<Long> hospitalIdsWithDepartment = hospitalDepartmentRepository
                .findByDepartmentId(request.getDepartmentId())
                .stream()
                .map(hd -> hd.getHospital().getId())
                .collect(Collectors.toList());

        if (hospitalIdsWithDepartment.isEmpty()) {
            throw new RuntimeException("No hospitals found with the requested department");
        }

        List<Hospital> hospitals = hospitalRepository.findAllById(hospitalIdsWithDepartment);

        List<HospitalRecommendationResponse> recommendations = hospitals.stream()
                .filter(hospital -> !weightCalculator.isHospitalExcluded(hospital))
                .<HospitalRecommendationResponse>map(hospital -> {
                    double score = weightCalculator.calculateScore(
                            hospital,
                            request.getLatitude(),
                            request.getLongitude()
                    );

                    double distance = calculateDistance(
                            request.getLatitude(),
                            request.getLongitude(),
                            hospital.getLatitude(),
                            hospital.getLongitude()
                    );

                    return HospitalRecommendationResponse.builder()
                            .hospitalId(hospital.getId())
                            .hospitalName(hospital.getName())
                            .address(hospital.getAddress())
                            .district(hospital.getDistrict())
                            .distance(distance)
                            .intensity(hospital.getCurrentIntensity())
                            .operationalStatus(hospital.getOperationalStatus())
                            .score(score)
                            .recommendationReason(generateRecommendationReason(hospital, distance, score))
                            .build();
                })
                .sorted(Comparator.comparing(HospitalRecommendationResponse::getScore))
                .limit(request.getMaxResults() != null ? request.getMaxResults() : 5)
                .collect(Collectors.toList());

        return recommendations;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c;
    }

    private String generateRecommendationReason(Hospital hospital, double distance, double score) {
        StringBuilder reason = new StringBuilder();
        reason.append("Distance: ").append(String.format("%.2f", distance)).append(" km");
        
        if (hospital.getCurrentIntensity() != null && hospital.getCurrentIntensity() < 0.5) {
            reason.append(", Low intensity");
        }
        
        if (hospital.getOperationalStatus() == com.hospital.management.demo.model.enums.OperationalStatus.OPERATIONAL) {
            reason.append(", Fully operational");
        }
        
        return reason.toString();
    }
}

