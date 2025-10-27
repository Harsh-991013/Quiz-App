package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "assignment_statuses")
public class AssignmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_status_id")
    private Integer assignmentStatusId;

    @Column(name = "status_key", nullable = false, unique = true)
    private String statusKey;

    @Column(name = "display_name")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;
}