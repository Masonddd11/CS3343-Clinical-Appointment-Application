package com.hospital.management.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "symptoms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symptom; // e.g., "kidney pain", "chest pain"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_department_id", nullable = false)
    private Department recommendedDepartment;

    @Column
    @Builder.Default
    private Integer priority = 1; // for ranking when multiple matches exist

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

