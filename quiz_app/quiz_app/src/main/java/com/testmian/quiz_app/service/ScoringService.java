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
    private final QuizQuestionRepository quizQuestionRepository;
    private final AuditService auditService;

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
        Score overallScore = new Score();
        overallScore.setAttempt(attempt);
        overallScore.setCandidate(attempt.getAssignment().getCandidate());
        overallScore.setCandidateName(attempt.getAssignment().getCandidate().getFullName());
        overallScore.setTotalQuestions(totalQuestions);
        overallScore.setCorrectAnswers(correctAnswers);
        overallScore.setScore(totalScore);
        overallScore.setCreatedAt(LocalDateTime.now());

        scoreRepository.save(overallScore);

        // Calculate category-wise scores
        calculateCategoryWiseScores(attempt, answers);

        // Log the scoring action
        auditService.logQuizAction(attempt.getAssignment().getCandidate(), "SCORE_CALCULATE", overallScore.getScoreId(),
            "Calculated total score: " + totalScore + " (" + correctAnswers + "/" + totalQuestions + ")", null, null, null);
    }

    private void calculateCategoryWiseScores(QuizAttempt attempt, List<AttemptAnswer> answers) {
        // Category-wise scoring will be implemented when the database schema includes category relationships
        // For now, we maintain the existing overall scoring functionality
        // Future enhancement: Add category-based scoring breakdown
    }

}