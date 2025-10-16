package com.testmian.quiz_app.service;

import com.testmian.quiz_app.dto.NotificationMessage;
import com.testmian.quiz_app.entity.Notification;
import com.testmian.quiz_app.entity.User;
import com.testmian.quiz_app.repository.NotificationRepository;
import com.testmian.quiz_app.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Constructor for dependency injection verification
    public NotificationService(KafkaTemplate<String, NotificationMessage> kafkaTemplate,
                              NotificationRepository notificationRepository,
                              UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;

        System.out.println("🚀🚀🚀 NOTIFICATION SERVICE STARTED - KafkaTemplate available: " + (kafkaTemplate != null) + " 🚀🚀🚀");
    }

    public void sendQuizCompletionNotification(Integer candidateId, String quizTitle, Float score, Integer correctAnswers, Integer totalQuestions) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String subject = "Quiz Completed: " + quizTitle;
        String message = String.format(
            "Dear %s,\n\nYou have successfully completed the quiz '%s'.\n\n" +
            "Score: %.1f%%\nCorrect Answers: %d/%d\n\nBest regards,\nQuiz Team",
            candidate.getFullName(), quizTitle, score, correctAnswers, totalQuestions
        );

        NotificationMessage notificationMsg = new NotificationMessage(
            candidateId,
            candidate.getEmail(),
            candidate.getFullName(),
            NotificationMessage.NotificationType.QUIZ_COMPLETED,
            subject,
            message,
            new QuizResultData(quizTitle, score, correctAnswers, totalQuestions)
        );

        // Send to Kafka
        kafkaTemplate.send("email-notifications", notificationMsg);

        // Save to database
        saveNotification(candidate, Notification.NotificationType.QuizStart, message);
    }

    public void sendQuizAutoSubmitNotification(Integer candidateId, String quizTitle, Float score) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String subject = "Quiz Auto-Submitted: " + quizTitle;
        String message = String.format(
            "Dear %s,\n\nYour quiz '%s' has been automatically submitted due to time expiration.\n\n" +
            "Final Score: %.1f%%\n\nBest regards,\nQuiz Team",
            candidate.getFullName(), quizTitle, score
        );

        NotificationMessage notificationMsg = new NotificationMessage(
            candidateId,
            candidate.getEmail(),
            candidate.getFullName(),
            NotificationMessage.NotificationType.QUIZ_AUTO_SUBMITTED,
            subject,
            message,
            new QuizAutoSubmitData(quizTitle, score)
        );

        // Send to Kafka
        System.out.println("🚀 NotificationService: Sending message to Kafka topic 'email-notifications'");
        System.out.println("🚀 Recipient: " + notificationMsg.getRecipientEmail());
        System.out.println("🚀 Type: " + notificationMsg.getType());

        kafkaTemplate.send("email-notifications", notificationMsg);

        System.out.println("✅ NotificationService: Message sent to Kafka successfully");

        // Save to database
        saveNotification(candidate, Notification.NotificationType.QuizAutoSubmitted, message);
    }

    public void sendQuizStartNotification(Integer candidateId, String quizTitle) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String subject = "Quiz Started: " + quizTitle;
        String message = String.format(
            "Dear %s,\n\nYou have successfully started the quiz '%s'.\n\n" +
            "Good luck! Make sure to complete all questions within the time limit.\n\n" +
            "Best regards,\nQuiz Team",
            candidate.getFullName(), quizTitle
        );

        NotificationMessage notificationMsg = new NotificationMessage(
            candidateId,
            candidate.getEmail(),
            candidate.getFullName(),
            NotificationMessage.NotificationType.QUIZ_START, // Fixed: Correct type
            subject,
            message,
            new QuizStartData(quizTitle)
        );

        // Send to Kafka
        System.out.println("🚀 NotificationService: Sending quiz start message to Kafka topic 'email-notifications'");
        System.out.println("🚀 Recipient: " + notificationMsg.getRecipientEmail());
        System.out.println("🚀 Type: " + notificationMsg.getType());

        kafkaTemplate.send("email-notifications", notificationMsg);

        System.out.println("✅ NotificationService: Quiz start message sent to Kafka successfully");

        // Save to database
        saveNotification(candidate, Notification.NotificationType.QuizResult, message);
    }

    private void saveNotification(User recipient, Notification.NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setMessage(message);
        notification.setStatus(Notification.Status.Sent);
        notificationRepository.save(notification);
    }

    // Inner classes for notification data
    public static class QuizResultData {
        public String quizTitle;
        public Float score;
        public Integer correctAnswers;
        public Integer totalQuestions;

        public QuizResultData(String quizTitle, Float score, Integer correctAnswers, Integer totalQuestions) {
            this.quizTitle = quizTitle;
            this.score = score;
            this.correctAnswers = correctAnswers;
            this.totalQuestions = totalQuestions;
        }
    }

    public static class QuizAutoSubmitData {
        public String quizTitle;
        public Float score;

        public QuizAutoSubmitData(String quizTitle, Float score) {
            this.quizTitle = quizTitle;
            this.score = score;
        }
    }

    public static class QuizStartData {
        public String quizTitle;

        public QuizStartData(String quizTitle) {
            this.quizTitle = quizTitle;
        }
    }
}