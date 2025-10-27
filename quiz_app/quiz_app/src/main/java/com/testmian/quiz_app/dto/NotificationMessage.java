package com.testmian.quiz_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Integer recipientId;
    private String recipientEmail;
    private String recipientName;
    private NotificationType type;
    private String subject;
    private String message;
    private Object data; // Additional data for email templates

    public enum NotificationType {
        QUIZ_START,
        QUIZ_COMPLETED,
        QUIZ_AUTO_SUBMITTED,
        QUIZ_ALERT
    }
}