package com.hospital.management.demo.integration.ae;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AeIntensityCalculatorTest {

    private final AeIntensityCalculator calculator = new AeIntensityCalculator();

    @Test
    void parseMinutesHandlesMinutesAndHours() {
        assertThat(calculator.parseMinutes("15 minutes")).isEqualTo(15);
        assertThat(calculator.parseMinutes("2 hours")).isEqualTo(120);
        assertThat(calculator.parseMinutes("less than 30 minutes")).isEqualTo(30);
        assertThat(calculator.parseMinutes("0 minute")).isZero();
    }

    @Test
    void calculateIntensityBoundsBetweenZeroAndOne() {
        AeWaitingTimeEntry entry = new AeWaitingTimeEntry(
                "Test Hospital",
                "0 minute",
                "N",
                "less than 15 minutes",
                "N",
                "30 minutes",
                "60 minutes",
                "2 hours",
                "4 hours"
        );
        double intensity = calculator.calculateIntensity(entry);
        assertThat(intensity).isGreaterThan(0).isLessThanOrEqualTo(1);
    }
}

