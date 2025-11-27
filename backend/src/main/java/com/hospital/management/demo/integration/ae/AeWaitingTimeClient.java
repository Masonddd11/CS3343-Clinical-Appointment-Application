package com.hospital.management.demo.integration.ae;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AeWaitingTimeClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String REMOTE_URL = "https://www.ha.org.hk/opendata/aed/aedwtdata2-en.json";
    private static final String LOCAL_FALLBACK = "a&e_waiting_time.Json";

    public AeWaitingTimeResponse fetchLatest() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(REMOTE_URL, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), AeWaitingTimeResponse.class);
            }
            log.error("A&E waiting time API returned non-success status: {}", response.getStatusCode());
        } catch (Exception ex) {
            log.error("Failed to fetch remote A&E data. Falling back to local snapshot.", ex);
        }
        return loadFromLocalSnapshot();
    }

    private AeWaitingTimeResponse loadFromLocalSnapshot() {
        ClassPathResource resource = new ClassPathResource(LOCAL_FALLBACK);
        if (!resource.exists()) {
            log.error("Local A&E snapshot {} missing.", LOCAL_FALLBACK);
            return new AeWaitingTimeResponse(null, null);
        }
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, AeWaitingTimeResponse.class);
        } catch (IOException e) {
            log.error("Failed to parse local A&E snapshot.", e);
            return new AeWaitingTimeResponse(null, null);
        }
    }
}

