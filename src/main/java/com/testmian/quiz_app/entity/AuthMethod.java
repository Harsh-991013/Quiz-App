package com.testmian.quiz_app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_methods")
public class AuthMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_method_id")
    private Long id;

    @Column(name = "method_key", nullable = false, unique = true, length = 50)
    private String methodKey;

    @Column(name = "display_name", length = 100)
    private String displayName;

    // Constructors
    public AuthMethod() {}
    public AuthMethod(String methodKey, String displayName) {
        this.methodKey = methodKey;
        this.displayName = displayName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMethodKey() { return methodKey; }
    public void setMethodKey(String methodKey) { this.methodKey = methodKey; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

