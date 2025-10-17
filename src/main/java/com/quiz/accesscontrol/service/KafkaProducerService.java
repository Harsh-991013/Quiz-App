package com.quiz.accesscontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

	 @Autowired(required=false)
    private KafkaTemplate<String, String> kafkaTemplate;

	 public void sendEmailEvent(String message) {
	        if (kafkaTemplate == null) {
	            System.out.println("⚠️ Kafka is disabled — skipping email event");
	            return;
	        }

	        kafkaTemplate.send("invite-topic", message);
	        System.out.println("✅ Message sent to Kafka topic");
	    }
}

