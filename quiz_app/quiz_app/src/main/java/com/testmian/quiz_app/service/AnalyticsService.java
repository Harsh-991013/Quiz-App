package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAssignmentRepository quizAssignmentRepository;
    private final ScoreRepository scoreRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public Map<String, Object> getQuizSummary(Integer quizId) {
        List<QuizAssignment> assignments = quizAssignmentRepository.findAll().stream()
                .filter(a -> a.getQuiz().getQuizId().equals(quizId))
                .collect(Collectors.toList());

        List<QuizAttempt> attempts = quizAttemptRepository.findAll().stream()
                .filter(a -> a.getAssignment().getQuiz().getQuizId().equals(quizId))
                .collect(Collectors.toList());

        List<Score> scores = scoreRepository.findAll().stream()
                .filter(s -> s.getAttempt().getAssignment().getQuiz().getQuizId().equals(quizId))
                .collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAssignments", assignments.size());
        summary.put("totalAttempts", attempts.size());
        summary.put("completedAttempts", attempts.stream().filter(a -> a.getEndTime() != null).count());
        summary.put("averageScore", scores.stream().mapToDouble(Score::getScore).average().orElse(0.0));
        summary.put("highestScore", scores.stream().mapToDouble(Score::getScore).max().orElse(0.0));
        summary.put("lowestScore", scores.stream().mapToDouble(Score::getScore).min().orElse(0.0));

        return summary;
    }

    public Map<String, Object> getCandidatePerformance(Integer candidateId) {
        List<Score> scores = scoreRepository.findByCandidate_UserId(candidateId);

        Map<String, Object> performance = new HashMap<>();
        performance.put("totalQuizzes", scores.size());
        performance.put("averageScore", scores.stream().mapToDouble(Score::getScore).average().orElse(0.0));
        performance.put("totalCorrectAnswers", scores.stream().mapToInt(Score::getCorrectAnswers).sum());
        performance.put("totalQuestions", scores.stream().mapToInt(Score::getTotalQuestions).sum());
        performance.put("scores", scores);

        return performance;
    }

    public Map<String, Object> getQuestionStats(Integer questionId) {
        List<AttemptAnswer> answers = attemptAnswerRepository.findAll().stream()
                .filter(a -> a.getQuestion().getQuestionId().equals(questionId))
                .collect(Collectors.toList());

        long totalAttempts = answers.size();
        long correctAnswers = answers.stream().filter(AttemptAnswer::getIsCorrect).count();
        double successRate = totalAttempts > 0 ? (double) correctAnswers / totalAttempts * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("questionId", questionId);
        stats.put("totalAttempts", totalAttempts);
        stats.put("correctAnswers", correctAnswers);
        stats.put("successRate", successRate);

        return stats;
    }

    public Map<String, Object> getDashboardOverview() {
        List<QuizAttempt> allAttempts = quizAttemptRepository.findAll();
        List<Score> allScores = scoreRepository.findAll();
        List<QuizAssignment> allAssignments = quizAssignmentRepository.findAll();

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalQuizzes", allAssignments.stream().map(a -> a.getQuiz().getQuizId()).distinct().count());
        overview.put("totalCandidates", allAssignments.stream().map(a -> a.getCandidate().getUserId()).distinct().count());
        overview.put("totalAttempts", allAttempts.size());
        overview.put("completedAttempts", allAttempts.stream().filter(a -> a.getEndTime() != null).count());
        overview.put("averageScore", allScores.stream().mapToDouble(Score::getScore).average().orElse(0.0));
        overview.put("totalQuestions", quizQuestionRepository.findAll().size());

        return overview;
    }

    public List<Map<String, Object>> getLatestTestRecords() {
        // Get all completed quiz attempts ordered by end time (most recent first)
        List<QuizAttempt> completedAttempts = quizAttemptRepository.findAll().stream()
                .filter(attempt -> attempt.getEndTime() != null)
                .sorted((a1, a2) -> a2.getEndTime().compareTo(a1.getEndTime())) // Most recent first
                .collect(Collectors.toList());

        return completedAttempts.stream().map(attempt -> {
            Map<String, Object> record = new HashMap<>();
            record.put("attemptId", attempt.getAttemptId());
            record.put("candidateId", attempt.getAssignment().getCandidate().getUserId());
            record.put("candidateName", attempt.getAssignment().getCandidate().getFullName());
            record.put("candidateEmail", attempt.getAssignment().getCandidate().getEmail());
            record.put("quizId", attempt.getAssignment().getQuiz().getQuizId());
            record.put("quizTitle", attempt.getAssignment().getQuiz().getTitle());
            record.put("startTime", attempt.getStartTime());
            record.put("endTime", attempt.getEndTime());
            record.put("totalScore", attempt.getTotalScore());
            record.put("autoSubmitted", attempt.getAutoSubmitted());

            // Get score details if available
            List<Score> scores = scoreRepository.findByAttempt_AttemptId(attempt.getAttemptId());
            if (!scores.isEmpty()) {
                Score score = scores.get(0);
                record.put("correctAnswers", score.getCorrectAnswers());
                record.put("totalQuestions", score.getTotalQuestions());
                record.put("percentage", score.getScore());
            }

            return record;
        }).collect(Collectors.toList());
    }
}