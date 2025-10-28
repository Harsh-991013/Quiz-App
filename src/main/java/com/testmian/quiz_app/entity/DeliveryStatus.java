package com.testmian.quiz_app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_statuses")
public class DeliveryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_status_id")
    private Long deliveryStatusId;

    @Column(name = "status_key", length = 50, unique = true, nullable = false)
    private String statusKey;

    @Column(name = "display_name", length = 100)
    private String displayName;

    // Constructors
    public DeliveryStatus() {}

    public DeliveryStatus(String statusKey, String displayName) {
        this.statusKey = statusKey;
        this.displayName = displayName;
    }

    // Getters and Setters
    public Long getDeliveryStatusId() {
        return deliveryStatusId;
    }

    public void setDeliveryStatusId(Long deliveryStatusId) {
        this.deliveryStatusId = deliveryStatusId;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
