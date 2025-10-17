package com.quiz.accesscontrol.service;

//Java utilities
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.accesscontrol.dto.InviteEmailPayload;

import jakarta.mail.Authenticator;
//Jakarta Mail (email sending)
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailConsumerService {

    @KafkaListener(topics = "invite-topic", groupId = "email-group")
    public void consumeInviteMessage(ConsumerRecord<String, String> record) {
        try {
            System.out.println("📩 Received from Kafka: " + record.value());
            
            // Parse message (assuming JSON with email, name, token, etc.)
            ObjectMapper mapper = new ObjectMapper();
            InviteEmailPayload payload = mapper.readValue(record.value(), InviteEmailPayload.class);
            
            sendRealEmail(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRealEmail(InviteEmailPayload payload) throws Exception {
        // ✅ Gmail SMTP setup
        String sender = "yourgmail@gmail.com";
        String appPassword = "your-google-app-password";  // from Google account → Security → App Passwords

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(payload.getEmail()));
        message.setSubject("🎉 Admin Invitation");
        message.setText("Hello " + payload.getFullName() + ",\n\n"
                + "You have been invited as an Admin.\n\n"
                + "Activate your account using this link:\n"
                + "https://yourapp.com/activate?token=" + payload.getMagicToken() + "\n\n"
                + "This link expires in 24 hours.\n\n"
                + "Best regards,\nSuper Admin Team");

        Transport.send(message);
        System.out.println("✅ Invitation email sent to " + payload.getEmail());
    }
}

