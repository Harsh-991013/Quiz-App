package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_statuses")
public class DeliveryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_status_id")
    private Integer deliveryStatusId;

    @Column(name = "status_key", nullable = false, unique = true)
    private String statusKey;

    @Column(name = "display_name")
    private String displayName;
}