package com.quiz.accesscontrol.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "magic_links")
public class MagicLink {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long magicLinkId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", length = 255, nullable = false, unique = true)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 20, nullable = false)
    private Purpose purpose;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used")
    private Boolean used = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 10)
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    public enum Purpose { LOGIN, ACTIVATE_ADMIN, RESET_PASSWORD,QUIZ_ACCESS, INVITE }
    public enum DeliveryStatus { SENT, FAILED }

    public Long getMagicLinkId() {
		return magicLinkId;
	}
	public void setMagicLinkId(Long magicLinkId) {
		this.magicLinkId = magicLinkId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTokenHash() {
		return tokenHash;
	}
	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}
	public Purpose getPurpose() {
		return purpose;
	}
	public void setPurpose(Purpose purpose) {
		this.purpose = purpose;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	public Boolean getUsed() {
		return used;
	}
	public void setUsed(Boolean used) {
		this.used = used;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUsedAt() {
		return usedAt;
	}
	public void setUsedAt(LocalDateTime usedAt) {
		this.usedAt = usedAt;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
}
