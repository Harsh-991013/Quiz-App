package com.example.quiz.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "email-events", groupId = "email-logging-group")
    public void consumeEmailEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String type = event.path("type").asText("UNKNOWN");
            String toEmail = event.path("toEmail").asText("N/A");
            String status = event.path("status").asText("N/A");
            String error = event.path("error").asText("");
            String timestamp = event.path("timestamp").asText("");

            if ("SUCCESS".equalsIgnoreCase(status)) {
                log.info("📧 Email Event Received [{}]: to={}, status={}, time={}", type, toEmail, status, timestamp);
            } else {
                log.warn("Email Event Failed [{}]: to={}, status={}, error={}, time={}", type, toEmail, status, error, timestamp);
            }

        } catch (Exception e) {
            log.error(" Failed to parse email event JSON: {} | Raw message: {}", e.getMessage(), message);
        }
    }
}
