package com.testmian.quiz_app.service;

import com.testmian.quiz_app.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(NotificationMessage message) {
        try {
            System.out.println("📧 Attempting to send email to: " + message.getRecipientEmail());
            System.out.println("📧 Subject: " + message.getSubject());
            System.out.println("📧 Type: " + message.getType());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(message.getRecipientEmail());
            helper.setSubject(message.getSubject());

            // Use Thymeleaf template for HTML email
            Context context = new Context();
            context.setVariable("recipientName", message.getRecipientName());
            context.setVariable("message", message.getMessage());
            context.setVariable("data", message.getData());

            String templateName = getTemplateName(message.getType());
            System.out.println("📧 Using template: email/" + templateName);

            String htmlContent = templateEngine.process("email/" + templateName, context);
            System.out.println("📧 Template processed successfully");

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully to: " + message.getRecipientEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + message.getRecipientEmail() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String getTemplateName(NotificationMessage.NotificationType type) {
        return switch (type) {
            case QUIZ_START -> "quiz-started";
            case QUIZ_COMPLETED -> "quiz-completed";
            case QUIZ_AUTO_SUBMITTED -> "quiz-auto-submitted";
            case QUIZ_ALERT -> "quiz-alert";
            default -> "quiz-completed";
        };
    }
}