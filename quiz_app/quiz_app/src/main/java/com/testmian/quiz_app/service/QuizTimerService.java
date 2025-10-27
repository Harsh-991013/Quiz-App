package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.QuizAttempt;
import com.testmian.quiz_app.repository.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizTimerService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizMonitoringService quizMonitoringService;
    private final AuditService auditService;

    /**
     * Check for quiz timeouts every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    @Transactional
    public void checkQuizTimeouts() {
        System.out.println("Checking for quiz timeouts at: " + LocalDateTime.now());

        // Find all active quiz attempts (not submitted yet)
        List<QuizAttempt> activeAttempts = quizAttemptRepository.findAll().stream()
                .filter(attempt -> attempt.getEndTime() == null)
                .filter(attempt -> attempt.getStartTime() != null)
                .toList();

        for (QuizAttempt attempt : activeAttempts) {
            checkAndHandleTimeout(attempt);
        }
    }

    private void checkAndHandleTimeout(QuizAttempt attempt) {
        try {
            LocalDateTime startTime = attempt.getStartTime();
            Integer durationMinutes = attempt.getAssignment().getQuiz().getDurationMinutes();

            if (startTime == null || durationMinutes == null) {
                return; // Cannot check timeout without start time or duration
            }

            LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
            LocalDateTime now = LocalDateTime.now();

            // Check if quiz has timed out
            if (now.isAfter(endTime)) {
                handleQuizTimeout(attempt);
            } else {
                // Check for warnings (e.g., 5 minutes remaining, 1 minute remaining)
                checkTimeWarnings(attempt, startTime, durationMinutes, now);
            }
        } catch (Exception e) {
            System.err.println("Error checking timeout for attempt " + attempt.getAttemptId() + ": " + e.getMessage());
        }
    }

    private void checkTimeWarnings(QuizAttempt attempt, LocalDateTime startTime, Integer durationMinutes, LocalDateTime now) {
        long elapsedMinutes = ChronoUnit.MINUTES.between(startTime, now);
        long remainingMinutes = durationMinutes - elapsedMinutes;

        // Send warning at 5 minutes remaining
        if (remainingMinutes == 5 && !hasWarningBeenSent(attempt, "TIME_WARNING_5_MIN")) {
            sendTimeWarning(attempt, 5, "5 minutes remaining");
        }
        // Send warning at 1 minute remaining
        else if (remainingMinutes == 1 && !hasWarningBeenSent(attempt, "TIME_WARNING_1_MIN")) {
            sendTimeWarning(attempt, 1, "1 minute remaining");
        }
    }

    private boolean hasWarningBeenSent(QuizAttempt attempt, String warningType) {
        // In a real implementation, you might want to track sent warnings in the database
        // For now, we'll use a simple approach
        return false; // Always send warnings for simplicity
    }

    private void sendTimeWarning(QuizAttempt attempt, int minutesRemaining, String message) {
        try {
            // Create a time warning violation
            quizMonitoringService.recordViolation(attempt.getAttemptId(), attempt.getAssignment().getCandidate().getUserId(),
                "TIME_WARNING", "Quiz time warning: " + message, null, null);

            auditService.logQuizAction(attempt.getAssignment().getCandidate(), "TIME_WARNING",
                attempt.getAttemptId(), "Time warning sent: " + message, null, null, null);

            System.out.println("Time warning sent for attempt " + attempt.getAttemptId() + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send time warning: " + e.getMessage());
        }
    }

    private void handleQuizTimeout(QuizAttempt attempt) {
        try {
            System.out.println("Auto-submitting quiz attempt " + attempt.getAttemptId() + " due to timeout");

            // Auto-submit the quiz using the monitoring service
            quizMonitoringService.recordViolation(attempt.getAttemptId(), attempt.getAssignment().getCandidate().getUserId(),
                "QUIZ_TIMEOUT", "Quiz auto-submitted due to timeout", null, null);

            auditService.logQuizAction(attempt.getAssignment().getCandidate(), "QUIZ_TIMEOUT",
                attempt.getAttemptId(), "Quiz auto-submitted due to timeout", null, null, null);

            System.out.println("Quiz attempt " + attempt.getAttemptId() + " auto-submitted due to timeout");
        } catch (Exception e) {
            System.err.println("Failed to auto-submit quiz due to timeout: " + e.getMessage());
        }
    }

    /**
     * Get remaining time for a quiz attempt in minutes
     */
    public long getRemainingTimeMinutes(Integer attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getEndTime() != null) {
            return 0; // Quiz already ended
        }

        LocalDateTime startTime = attempt.getStartTime();
        Integer durationMinutes = attempt.getAssignment().getQuiz().getDurationMinutes();

        if (startTime == null || durationMinutes == null) {
            return 0;
        }

        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(endTime)) {
            return 0; // Already timed out
        }

        return ChronoUnit.MINUTES.between(now, endTime);
    }

    /**
     * Get elapsed time for a quiz attempt in minutes
     */
    public long getElapsedTimeMinutes(Integer attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        LocalDateTime startTime = attempt.getStartTime();
        if (startTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.MINUTES.between(startTime, now);
    }

    /**
     * Check if a quiz attempt has timed out
     */
    public boolean hasTimedOut(Integer attemptId) {
        return getRemainingTimeMinutes(attemptId) <= 0;
    }
}