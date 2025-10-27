package com.testmian.quiz_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${app.kafka.topic.email-notifications}")
    private String emailNotificationsTopic;

    // Kafka configuration is handled by application.properties
    // Topics will be auto-created if not existing
}