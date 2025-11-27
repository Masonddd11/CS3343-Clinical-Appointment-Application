package com.hospital.management.demo.integration.district;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistrictMapper {

    private final ObjectMapper objectMapper;
    private Map<String, String> mapping = new HashMap<>();

    private static final String DISTRICT_FILE = "district-mapping.json";

    @PostConstruct
    public void loadMapping() {
        ClassPathResource resource = new ClassPathResource(DISTRICT_FILE);
        if (!resource.exists()) {
            log.error("District mapping file {} not found.", DISTRICT_FILE);
            return;
        }
        try (InputStream is = resource.getInputStream()) {
            mapping = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to load district mapping file.", e);
            mapping = Collections.emptyMap();
        }
    }

    public String map(String hospitalName, String fallbackCluster) {
        return mapping.getOrDefault(hospitalName, normalizeCluster(fallbackCluster));
    }

    private String normalizeCluster(String cluster) {
        if (cluster == null || cluster.isBlank()) {
            return "Unknown";
        }
        return cluster
                .replace(" Cluster", "")
                .replace("Hong Kong ", "")
                .replace("New Territories ", "")
                .replace("Kowloon ", "")
                .trim();
    }
}

