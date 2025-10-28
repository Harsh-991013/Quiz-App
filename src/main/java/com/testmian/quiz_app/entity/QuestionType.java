package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_types")
public class QuestionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionTypeId;

    @Column(nullable = false, unique = true)
    private String typeKey;

    private String displayName;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Integer getQuestionTypeId() { return questionTypeId; }
    public void setQuestionTypeId(Integer questionTypeId) { this.questionTypeId = questionTypeId; }

    public String getTypeKey() { return typeKey; }
    public void setTypeKey(String typeKey) { this.typeKey = typeKey; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
