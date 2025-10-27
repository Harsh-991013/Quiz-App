package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "quiz_policies")
public class QuizPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "max_warnings_before_auto_submit", nullable = false)
    private Integer maxWarningsBeforeAutoSubmit = 3;

    @Column(name = "auto_submit_on_multi_device", nullable = false)
    private Boolean autoSubmitOnMultiDevice = true;

    @Column(name = "auto_submit_on_timer_expiry", nullable = false)
    private Boolean autoSubmitOnTimerExpiry = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}