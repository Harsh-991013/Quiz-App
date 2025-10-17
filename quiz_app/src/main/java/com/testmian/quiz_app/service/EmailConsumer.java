package com.testmian.quiz_app.service;

import com.testmian.quiz_app.dto.NotificationMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
        System.out.println("📨 EmailConsumer initialized with EmailService: " + (emailService != null));
    }

    @KafkaListener(topics = "${app.kafka.topic.email-notifications}", groupId = "quiz-notification-group")
    public void processEmailNotification(NotificationMessage message) {
        try {
            System.out.println("📨 Kafka Consumer: Processing email notification");
            System.out.println("📨 Recipient: " + message.getRecipientEmail());
            System.out.println("📨 Type: " + message.getType());
            System.out.println("📨 Subject: " + message.getSubject());

            emailService.sendEmail(message);

            System.out.println("✅ Kafka Consumer: Email processing completed successfully");

        } catch (Exception e) {
            System.err.println("❌ Kafka Consumer: Failed to process email notification: " + e.getMessage());
            e.printStackTrace();
            // In a production system, you might want to:
            // 1. Send to a dead letter topic
            // 2. Implement retry logic
            // 3. Update notification status to FAILED
            throw e; // Re-throw to let Kafka handle retry/dead letter
        }
    }
}