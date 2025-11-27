package com.hospital.management.demo.integration.ae;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AeWaitingTimeEntry(
        @JsonProperty("hospName") String hospitalName,
        @JsonProperty("t1wt") String t1WaitingTime,
        @JsonProperty("manageT1case") String manageT1Case,
        @JsonProperty("t2wt") String t2WaitingTime,
        @JsonProperty("manageT2case") String manageT2Case,
        @JsonProperty("t3p50") String t3Median,
        @JsonProperty("t3p95") String t3P95,
        @JsonProperty("t45p50") String t45Median,
        @JsonProperty("t45p95") String t45P95
) {
}

