package com.hospital.management.demo.model.entity;

import com.hospital.management.demo.model.enums.OperationalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private String district;

    @Column
    private Integer capacity;

    @Column(name = "current_intensity")
    @Builder.Default
    private Double currentIntensity = 0.0; // 0.0 = low, 1.0 = high/congested

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status")
    @Builder.Default
    private OperationalStatus operationalStatus = OperationalStatus.OPERATIONAL;

    @Column(name = "closure_reason")
    private String closureReason;

    @Column(name = "closure_start_date")
    private LocalDateTime closureStartDate;

    @Column(name = "closure_end_date")
    private LocalDateTime closureEndDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

