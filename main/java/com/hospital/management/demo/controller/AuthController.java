package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.AuthResponse;
import com.hospital.management.demo.dto.LoginRequest;
import com.hospital.management.demo.dto.RegisterRequest;
import com.hospital.management.demo.dto.UserInfoResponse;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }
}

