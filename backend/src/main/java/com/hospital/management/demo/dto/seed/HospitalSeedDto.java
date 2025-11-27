package com.hospital.management.demo.dto.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HospitalSeedDto(
        @JsonProperty("cluster_eng") String clusterEng,
        @JsonProperty("institution_eng") String institutionEng,
        @JsonProperty("with_AE_service_eng") String withAeServiceEng,
        @JsonProperty("address_eng") String addressEng,
        @JsonProperty("latitude") Double latitude,
        @JsonProperty("longitude") Double longitude
) {
    public boolean hasAccidentAndEmergency() {
        return "yes".equalsIgnoreCase(withAeServiceEng);
    }
}

