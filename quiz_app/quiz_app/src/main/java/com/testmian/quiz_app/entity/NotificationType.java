package com.testmian.quiz_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_types")
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_id")
    private Integer notificationTypeId;

    @Column(name = "type_key", nullable = false, unique = true)
    private String typeKey;

    @Column(name = "display_name")
    private String displayName;
}