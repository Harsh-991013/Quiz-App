package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attemptId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private QuizAssignment assignment;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_score")
    private Float totalScore;

    @Column(name = "auto_submitted", nullable = false)
    private Boolean autoSubmitted;

    @Column(name = "violation_count", nullable = false)
    private Integer violationCount = 0;

    @Column(name = "warning_count", nullable = false)
    private Integer warningCount = 0;

    @ManyToOne
    @JoinColumn(name = "auto_submit_reason_id")
    private AutoSubmitReason autoSubmitReason;

    @ManyToOne
    @JoinColumn(name = "policy_status_id", nullable = false)
    private PolicyStatus policyStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}