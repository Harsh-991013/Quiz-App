package com.testmian.quiz_app.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testmian.quiz_app.config.MailConfig;
import com.testmian.quiz_app.dto.InviteEmailPayload;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailConsumerService {

    @Autowired
    private MailConfig mailConfig;

    private final ObjectMapper mapper;

    public EmailConsumerService() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ✅ 1️⃣ Listen for Admin Invitation
    @KafkaListener(topics = "invite-topic", groupId = "email-group")
    public void consumeInviteMessage(ConsumerRecord<String, String> record) {
        try {
            System.out.println("📩 Received Invite Message: " + record.value());

            InviteEmailPayload payload = mapper.readValue(record.value(), InviteEmailPayload.class);

            String template = Files.readString(Paths.get("src/main/resources/templates/invite-email-template.html"));
            template = template.replace("{{fullName}}", payload.getFullName())
                               .replace("{{magicToken}}", payload.getMagicToken());

            sendEmail(payload.getEmail(), "🎉 Admin Invitation", template, true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @KafkaListener(topics = "password-reset-topic", groupId = "email-group")
    public void consumePasswordResetMessage(ConsumerRecord<String, String> record) {
        try {
            System.out.println("📩 Received Password Reset Request: " + record.value());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> payload = mapper.readValue(
                    record.value(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            );

            // ✅ Load HTML Template
            String template = Files.readString(
                    Paths.get("src/main/resources/templates/password-reset-template.html")
            );

            // ✅ Replace placeholders with actual values
            template = template.replace("{{fullName}}", payload.get("fullName").toString())
                               .replace("{{magicToken}}", payload.get("magicToken").toString());

            String subject = "🔐 Password Reset Request";

            // ✅ Send as HTML email
            sendEmail(payload.get("email").toString(), subject, template, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String recipient, String subject, String body, boolean isHtml) throws Exception {
        String senderEmail = mailConfig.getSenderEmail();
      String senderPassword = mailConfig.getSenderPassword();

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.port", "587");

      Session session = Session.getInstance(props, new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(senderEmail, senderPassword);
          }
      });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        
        if (isHtml) {
            message.setContent(body, "text/html; charset=utf-8");
        } else {
            message.setText(body);
        }

        Transport.send(message);
    }

}
