package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final ScoreRepository scoreRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void calculateAndSaveScores(Integer attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        // Get all answers for this attempt
        List<AttemptAnswer> answers = attemptAnswerRepository.findByAttempt_AttemptId(attemptId);

        // Calculate overall score
        float totalScore = answers.stream()
                .filter(a -> a.getMarksObtained() != null)
                .map(AttemptAnswer::getMarksObtained)
                .reduce(0.0f, Float::sum);

        attempt.setTotalScore(totalScore);
        quizAttemptRepository.save(attempt);

        // Calculate total score summary
        int totalQuestions = answers.size();
        int correctAnswers = (int) answers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                .count();

        // Save overall score
        Score score = new Score();
        score.setAttempt(attempt);
        score.setCandidate(attempt.getAssignment().getCandidate());
        score.setCandidateName(attempt.getAssignment().getCandidate().getFullName());
        score.setTotalQuestions(totalQuestions);
        score.setCorrectAnswers(correctAnswers);
        score.setScore(totalScore);
        score.setCreatedAt(LocalDateTime.now());

        scoreRepository.save(score);

        // Log the scoring action
        logAuditAction(attempt.getAssignment().getCandidate(), "SCORE_CALCULATE", "Score", score.getScoreId(),
                "Calculated total score: " + totalScore + " (" + correctAnswers + "/" + totalQuestions + ")");
    }

    private void logAuditAction(User user, String actionType, String entityType, Integer entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setRole("Candidate"); // Simplified role
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}