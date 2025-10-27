package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "violation_actions")
public class ViolationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Integer actionId;

    @Column(name = "action_key", nullable = false, unique = true)
    private String actionKey;

    @Column(name = "display_name")
    private String displayName;
}