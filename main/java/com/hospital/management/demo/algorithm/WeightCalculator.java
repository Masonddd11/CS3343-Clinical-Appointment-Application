package com.hospital.management.demo.algorithm;

import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeightCalculator {

    private final DistanceCalculator distanceCalculator;

    @Value("${pathfinding.weight.distance:0.4}")
    private double distanceWeight;

    @Value("${pathfinding.weight.intensity:0.3}")
    private double intensityWeight;

    @Value("${pathfinding.weight.status:0.3}")
    private double statusWeight;

    @Value("${pathfinding.max.distance:50.0}")
    private double maxDistanceKm;

    public double calculateScore(Hospital hospital, double patientLat, double patientLon) {
        double distance = distanceCalculator.calculateDistance(
                patientLat, patientLon,
                hospital.getLatitude(), hospital.getLongitude()
        );

        double normalizedDistance = distanceCalculator.normalizeDistance(distance, maxDistanceKm);
        double normalizedIntensity = hospital.getCurrentIntensity() != null ? hospital.getCurrentIntensity() : 0.0;
        double statusPenalty = getStatusPenalty(hospital.getOperationalStatus());

        return (distanceWeight * normalizedDistance) +
               (intensityWeight * normalizedIntensity) +
               (statusWeight * statusPenalty);
    }

    private double getStatusPenalty(OperationalStatus status) {
        return switch (status) {
            case OPERATIONAL -> 0.0;
            case PARTIAL_SERVICE -> 0.5;
            case CLOSED_EPIDEMIC, CLOSED_OTHER -> 1.0;
        };
    }

    public boolean isHospitalExcluded(Hospital hospital) {
        return hospital.getOperationalStatus() == OperationalStatus.CLOSED_EPIDEMIC ||
               hospital.getOperationalStatus() == OperationalStatus.CLOSED_OTHER;
    }
}

