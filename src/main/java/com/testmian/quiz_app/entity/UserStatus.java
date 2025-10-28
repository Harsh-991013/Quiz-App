package com.testmian.quiz_app.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "user_statuses")
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;

    @Column(name = "status_key", nullable = false, unique = true, length = 50)
    private String statusKey;

    @Column(name = "display_name", length = 100)
    private String displayName;

    // Constructors
    public UserStatus() {}
    public UserStatus(String statusKey, String displayName) {
        this.statusKey = statusKey;
        this.displayName = displayName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatusKey() { return statusKey; }
    public void setStatusKey(String statusKey) { this.statusKey = statusKey; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

