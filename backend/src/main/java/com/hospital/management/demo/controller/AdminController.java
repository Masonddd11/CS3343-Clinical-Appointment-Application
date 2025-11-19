package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.DepartmentRequest;
import com.hospital.management.demo.dto.DepartmentResponse;
import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.dto.HospitalResponse;
import com.hospital.management.demo.dto.RegisterRequest;
import com.hospital.management.demo.dto.UserInfoResponse;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.service.DepartmentService;
import com.hospital.management.demo.service.HospitalService;
import com.hospital.management.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final HospitalService hospitalService;
    private final DepartmentService departmentService;

    @PostMapping("/users")
    public ResponseEntity<UserInfoResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream()
                .map(user -> UserInfoResponse.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList()));
    }

    @PostMapping("/hospitals")
    public ResponseEntity<HospitalResponse> createHospital(@Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(hospitalService.createHospital(request));
    }

    @PutMapping("/hospitals/{id}")
    public ResponseEntity<HospitalResponse> updateHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(hospitalService.updateHospital(id, request));
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }
}

