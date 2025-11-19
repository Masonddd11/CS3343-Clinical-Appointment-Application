package com.hospital.management.demo.dto;

import com.hospital.management.demo.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private UserRole role;
    private String message;
    private Long userId;
    private String token;
}

