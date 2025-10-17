package com.quiz.accesscontrol.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    public enum Status { INVITED, ACTIVE, INACTIVE, SUSPENDED }
    public enum AuthMethod { PASSWORD, MAGIC_LINK, OAUTH }
    public enum InviteStatus { PENDING, ACTIVATED, EXPIRED }
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "full_name", length = 100, nullable = true)
    private String fullName;

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = true)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", length = 20, nullable = true)
    private AuthMethod authMethod = AuthMethod.MAGIC_LINK;

    @Column(name = "is_invited")
    private Boolean isInvited = false;

    // who invited (nullable)
    @ManyToOne
    @JoinColumn(name = "invited_by", nullable = true)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_status", length = 20, nullable = true)
    private InviteStatus inviteStatus = InviteStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = true)
    private Status status = Status.INVITED;


    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "deleted_by", nullable = true)
    private User deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isActive() {
        return deletedAt == null;
    }

    // Getters and Setters (generated)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public AuthMethod getAuthMethod() { return authMethod; }
    public void setAuthMethod(AuthMethod authMethod) { this.authMethod = authMethod; }

    public Boolean getIsInvited() { return isInvited; }
    public void setIsInvited(Boolean isInvited) { this.isInvited = isInvited; }

    public User getInvitedBy() { return invitedBy; }
    public void setInvitedBy(User invitedBy) { this.invitedBy = invitedBy; }

    public InviteStatus getInviteStatus() { return inviteStatus; }
    public void setInviteStatus(InviteStatus inviteStatus) { this.inviteStatus = inviteStatus; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public User getDeletedBy() { return deletedBy; }
    public void setDeletedBy(User deletedBy) { this.deletedBy = deletedBy; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
