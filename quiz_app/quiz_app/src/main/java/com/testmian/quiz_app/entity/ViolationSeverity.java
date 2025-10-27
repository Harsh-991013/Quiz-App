package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "violation_severities")
public class ViolationSeverity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "severity_id")
    private Integer severityId;

    @Column(name = "severity_key", nullable = false, unique = true)
    private String severityKey;

    @Column(name = "display_name")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;
}