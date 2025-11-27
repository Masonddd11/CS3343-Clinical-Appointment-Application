package com.hospital.management.demo.integration.ae;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AeWaitingTimeResponse(
        @JsonProperty("waitTime") List<AeWaitingTimeEntry> entries,
        @JsonProperty("updateTime") String updateTime
) {
}

