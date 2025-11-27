package com.hospital.management.demo.algorithm;

import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeightCalculatorTest {

    private WeightCalculator weightCalculator;
    private DistanceCalculator distanceCalculator;

    private void setField(Object target, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        distanceCalculator = mock(DistanceCalculator.class);
        weightCalculator = new WeightCalculator(distanceCalculator);
        
        setField(weightCalculator, "distanceWeight", 0.4);
        setField(weightCalculator, "intensityWeight", 0.3);
        setField(weightCalculator, "statusWeight", 0.3);
        setField(weightCalculator, "maxDistanceKm", 50.0);
    }

    @Test
    void testCalculateScore_WithNullIntensity() {
        // Branch: hospital.getCurrentIntensity() == null
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(null)
                .build();

        when(distanceCalculator.calculateDistance(39.9042, 116.4074, 39.9042, 116.4074)).thenReturn(0.0);
        when(distanceCalculator.normalizeDistance(0.0, 50.0)).thenReturn(0.0);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.0, score, 0.0001);
    }

    @Test
    void testCalculateScore_WithIntensity() {
        // Branch: hospital.getCurrentIntensity() != null
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.5)
                .build();

        when(distanceCalculator.calculateDistance(39.9042, 116.4074, 39.9042, 116.4074)).thenReturn(0.0);
        when(distanceCalculator.normalizeDistance(0.0, 50.0)).thenReturn(0.0);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.15, score, 0.0001);
    }

    @Test
    void testCalculateScore_OperationalStatus() {
        // Branch: getStatusPenalty(OPERATIONAL) -> 0.0
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.0)
                .build();

        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(25.0);
        when(distanceCalculator.normalizeDistance(25.0, 50.0)).thenReturn(0.5);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.2, score, 0.0001);
    }

    @Test
    void testCalculateScore_PartialServiceStatus() {
        // Branch: getStatusPenalty(PARTIAL_SERVICE) -> 0.5
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.PARTIAL_SERVICE)
                .currentIntensity(0.0)
                .build();

        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(0.0);
        when(distanceCalculator.normalizeDistance(0.0, 50.0)).thenReturn(0.0);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.15, score, 0.0001);
    }

    @Test
    void testCalculateScore_ClosedEpidemicStatus() {
        // Branch: getStatusPenalty(CLOSED_EPIDEMIC) -> 1.0
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .currentIntensity(0.0)
                .build();

        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(0.0);
        when(distanceCalculator.normalizeDistance(0.0, 50.0)).thenReturn(0.0);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.3, score, 0.0001);
    }

    @Test
    void testCalculateScore_ClosedOtherStatus() {
        // Branch: getStatusPenalty(CLOSED_OTHER) -> 1.0
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_OTHER)
                .currentIntensity(0.0)
                .build();

        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(0.0);
        when(distanceCalculator.normalizeDistance(0.0, 50.0)).thenReturn(0.0);

        double score = weightCalculator.calculateScore(hospital, 39.9042, 116.4074);

        assertEquals(0.3, score, 0.0001);
    }

    @Test
    void testIsHospitalExcluded_ClosedEpidemic() {
        // Branch: hospital.getOperationalStatus() == CLOSED_EPIDEMIC
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .build();

        boolean excluded = weightCalculator.isHospitalExcluded(hospital);

        assertTrue(excluded);
    }

    @Test
    void testIsHospitalExcluded_ClosedOther() {
        // Branch: hospital.getOperationalStatus() == CLOSED_OTHER
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_OTHER)
                .build();

        boolean excluded = weightCalculator.isHospitalExcluded(hospital);

        assertTrue(excluded);
    }

    @Test
    void testIsHospitalExcluded_NotExcluded() {
        // Branch: hospital.getOperationalStatus() != CLOSED_EPIDEMIC && != CLOSED_OTHER
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();

        boolean excluded = weightCalculator.isHospitalExcluded(hospital);

        assertFalse(excluded);
    }
}

