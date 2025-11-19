package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.UserInfoResponse;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
public class PatientController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserInfoResponse> getProfile() {
        User user = userService.getCurrentUser();
        if (user.getRole() != UserRole.PATIENT && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }
}

