package com.testmian.quiz_app.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "magic_link_purposes")
public class MagicLinkPurpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purpose_id")
    private Long id;

    @Column(name = "purpose_key", nullable = false, unique = true, length = 50)
    private String purposeKey;

    @Column(name = "description", length = 100)
    private String description;

    // Constructors
    public MagicLinkPurpose() {}
    public MagicLinkPurpose(String purposeKey, String description) {
        this.purposeKey = purposeKey;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPurposeKey() { return purposeKey; }
    public void setPurposeKey(String purposeKey) { this.purposeKey = purposeKey; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

