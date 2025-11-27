package com.hospital.management.demo.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceCalculatorTest {

    private DistanceCalculator distanceCalculator;

    @BeforeEach
    void setUp() {
        distanceCalculator = new DistanceCalculator();
    }

    @Test
    void testCalculateDistance() {
        // Cover all instructions in calculateDistance method
        double lat1 = 39.9042;
        double lon1 = 116.4074;
        double lat2 = 31.2304;
        double lon2 = 121.4737;

        double distance = distanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        assertEquals(1068.0, distance, 50.0);
    }

    @Test
    void testNormalizeDistance_MaxDistanceIsZero() {
        // Branch: maxDistance == 0
        double result = distanceCalculator.normalizeDistance(100.0, 0.0);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void testNormalizeDistance_DistanceLessThanMax() {
        // Branch: distance / maxDistance < 1.0
        double result = distanceCalculator.normalizeDistance(50.0, 100.0);
        assertEquals(0.5, result, 0.0001);
    }

    @Test
    void testNormalizeDistance_DistanceGreaterThanOrEqualMax() {
        // Branch: distance / maxDistance >= 1.0, Math.min returns 1.0
        double result = distanceCalculator.normalizeDistance(150.0, 100.0);
        assertEquals(1.0, result, 0.0001);
    }
}
