package com.hospital.management.demo.scheduler;

import com.hospital.management.demo.integration.ae.AeIntensityCalculator;
import com.hospital.management.demo.integration.ae.AeWaitingTimeClient;
import com.hospital.management.demo.integration.ae.AeWaitingTimeEntry;
import com.hospital.management.demo.integration.ae.AeWaitingTimeResponse;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AeIntensityScheduler {

    private final AeWaitingTimeClient waitingTimeClient;
    private final AeIntensityCalculator intensityCalculator;
    private final HospitalRepository hospitalRepository;

    private LocalDateTime lastUpdate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mma");

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void refreshHospitalIntensity() {
        log.info("Starting A&E waiting time refresh job.");
        AeWaitingTimeResponse response = waitingTimeClient.fetchLatest();
        if (response == null || response.entries() == null || response.entries().isEmpty()) {
            log.warn("A&E waiting time payload empty. Skipping refresh.");
            return;
        }
        updateHospitals(response.entries());
        this.lastUpdate = parseUpdateTime(response.updateTime());
        log.info("A&E waiting time refresh completed at {}.", lastUpdate);
    }

    @Transactional
    protected void updateHospitals(List<AeWaitingTimeEntry> entries) {
        Map<String, AeWaitingTimeEntry> entryMap = entries.stream()
                .collect(Collectors.toMap(AeWaitingTimeEntry::hospitalName, entry -> entry, (a, b) -> a));

        List<Hospital> hospitals = hospitalRepository.findAll();
        for (Hospital hospital : hospitals) {
            AeWaitingTimeEntry entry = entryMap.get(hospital.getName());
            if (entry == null) {
                continue;
            }
            double intensity = intensityCalculator.calculateIntensity(entry);
            hospital.setCurrentIntensity(intensity);
        }
        hospitalRepository.saveAll(hospitals);
    }

    private LocalDateTime parseUpdateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(raw, FORMATTER);
        } catch (Exception ex) {
            log.warn("Failed to parse A&E update time: {}", raw, ex);
            return LocalDateTime.now();
        }
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}

