package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.HospitalRecommendationRequest;
import com.hospital.management.demo.dto.HospitalRecommendationResponse;
import com.hospital.management.demo.service.HospitalPathfinderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pathfinding")
@RequiredArgsConstructor
public class PathfindingController {

    private final HospitalPathfinderService hospitalPathfinderService;

    @PostMapping("/recommend")
    public ResponseEntity<List<HospitalRecommendationResponse>> recommendHospitals(
            @Valid @RequestBody HospitalRecommendationRequest request) {
        return ResponseEntity.ok(hospitalPathfinderService.recommendHospitals(request));
    }
}

