package com.example.quiz.service;

import com.example.quiz.entity.MagicLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendMagicLinkEmail(String toEmail, String recipientName, String magicLinkUrl,
                                   MagicLink.Purpose purpose, LocalDateTime expiresAt) {
        log.info("Attempting to send magic link email to: {}, purpose: {}", toEmail, purpose);
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("magicLinkUrl", magicLinkUrl);
            context.setVariable("purpose", purpose.toString());
            context.setVariable("expiresAt", expiresAt);

            String htmlContent = templateEngine.process("magic-link-email", context);
            log.debug("Email template processed successfully for magic link");

            sendEmail(toEmail, "Your Magic Link", htmlContent);
            log.info("Magic link email sent successfully to: {}", toEmail);

            // Send success to Kafka
            kafkaTemplate.send("email-events", Map.of(
                    "type", "MAGIC_LINK_SENT",
                    "toEmail", toEmail,
                    "purpose", purpose.toString(),
                    "status", "SUCCESS"
            ).toString());

        } catch (Exception e) {
            log.error("Failed to send magic link email to {}: {}", toEmail, e.getMessage(), e);

            // Send failure to Kafka
            kafkaTemplate.send("email-events", Map.of(
                    "type", "MAGIC_LINK_FAILED",
                    "toEmail", toEmail,
                    "purpose", purpose.toString(),
                    "status", "FAILED",
                    "error", e.getMessage()
            ).toString());
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Add company logo inline if exists
        try {
            ClassPathResource logoResource = new ClassPathResource("templates/company_logo.png");
            if (logoResource.exists()) {
                helper.addInline("company-logo", logoResource);
                log.debug("Company logo attached");
            }
        } catch (Exception e) {
            log.debug("Could not attach company logo: {}", e.getMessage());
        }

        mailSender.send(message);
        log.info("Email sent successfully to {}", to);
    }
}
