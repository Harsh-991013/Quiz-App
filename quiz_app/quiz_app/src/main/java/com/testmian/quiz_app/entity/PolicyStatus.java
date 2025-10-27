package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "policy_statuses")
public class PolicyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_status_id")
    private Integer policyStatusId;

    @Column(name = "status_key", nullable = false, unique = true)
    private String statusKey;

    @Column(name = "display_name")
    private String displayName;
}