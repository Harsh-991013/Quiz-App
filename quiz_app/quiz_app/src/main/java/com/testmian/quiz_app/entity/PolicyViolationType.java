package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "policy_violation_types")
public class PolicyViolationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_type_id")
    private Integer violationTypeId;

    @Column(name = "violation_key", nullable = false, unique = true)
    private String violationKey;

    @Column(name = "display_name")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "severity_id")
    private ViolationSeverity severity;
}