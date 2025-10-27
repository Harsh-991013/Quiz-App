package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "auto_submit_reasons")
public class AutoSubmitReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_id")
    private Integer reasonId;

    @Column(name = "reason_key", nullable = false, unique = true)
    private String reasonKey;

    @Column(name = "display_name")
    private String displayName;
}