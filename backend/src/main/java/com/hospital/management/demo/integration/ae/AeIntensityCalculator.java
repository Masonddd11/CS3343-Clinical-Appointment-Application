package com.hospital.management.demo.integration.ae;

import org.springframework.stereotype.Component;

@Component
public class AeIntensityCalculator {

    private static final double T3_MAX_MINUTES = 60.0; // target under 1 hour for semi-urgent cases
    private static final double T45_MAX_MINUTES = 240.0; // 4 hours for T4/5 cases
    private static final double DEFAULT_SCORE = 0.35;

    public double calculateIntensity(AeWaitingTimeEntry entry) {
        if (entry == null) {
            return DEFAULT_SCORE;
        }
        double t3Score = normalize(parseMinutes(entry.t3Median()), T3_MAX_MINUTES);
        double t45Score = normalize(parseMinutes(entry.t45Median()), T45_MAX_MINUTES);
        double weighted = (t3Score * 0.6) + (t45Score * 0.4);
        return Math.min(1.0, Math.max(0.05, roundToTwo(weighted)));
    }

    double parseMinutes(String raw) {
        if (raw == null || raw.isBlank()) {
            return -1;
        }
        String normalized = raw.trim().toLowerCase();
        if (normalized.contains("less than")) {
            normalized = normalized.replace("less than", "").trim();
        }
        normalized = normalized.replaceAll("minutes", "minute")
                .replaceAll("hours", "hour")
                .replaceAll("hrs", "hour")
                .replaceAll("mins", "minute")
                .trim();

        double multiplier = normalized.contains("hour") ? 60.0 : 1.0;
        String numericPortion = normalized.replaceAll("[^0-9.]+", "").trim();
        if (numericPortion.isEmpty()) {
            return 0;
        }
        double value = Double.parseDouble(numericPortion);
        return value * multiplier;
    }

    private double normalize(double minutes, double max) {
        if (minutes < 0) {
            return DEFAULT_SCORE;
        }
        return Math.min(1.0, minutes / max);
    }

    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

