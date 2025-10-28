package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "difficulty_levels")
public class Difficulty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer difficultyId;

    @Column(nullable = false, unique = true)
    private String difficultyKey;

    private String displayName;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Integer getDifficultyId() { return difficultyId; }
    public void setDifficultyId(Integer difficultyId) { this.difficultyId = difficultyId; }

    public String getDifficultyKey() { return difficultyKey; }
    public void setDifficultyKey(String difficultyKey) { this.difficultyKey = difficultyKey; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
