package com.quiz.accesscontrol.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Constructor injection ensures Spring injects KafkaTemplate
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailEvent(String message) {
        kafkaTemplate.send("invite-topic", message);
        System.out.println("✅ Message sent to Kafka topic");
    }
}


