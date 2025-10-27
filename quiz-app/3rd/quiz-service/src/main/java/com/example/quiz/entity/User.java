package com.example.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "role", "invitedBy", "deletedBy"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", columnDefinition = "enum('password','magic_link','oauth') default 'magic_link'")
    private AuthMethod authMethod = AuthMethod.magic_link;

    @Column(name = "is_invited", columnDefinition = "tinyint(1) default 0")
    private Boolean isInvited = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_status", columnDefinition = "enum('Pending','Activated','Expired') default 'Pending'")
    private InviteStatus inviteStatus = InviteStatus.Pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "enum('Active','Inactive','Suspended') default 'Inactive'")
    private UserStatus status = UserStatus.Inactive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    public enum AuthMethod { password, magic_link, oauth }
    public enum InviteStatus { Pending, Activated, Expired }
    public enum UserStatus { Active, Inactive, Suspended }
}
