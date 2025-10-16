package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.AttemptAnswerRepository;
import com.testmian.quiz_app.repository.QuizAttemptRepository;
import com.testmian.quiz_app.service.QuizAttemptService;
import com.testmian.quiz_app.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz-attempt")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final ScoringService scoringService;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @PostMapping("/start")
    public ResponseEntity<?> startQuizAttempt(@RequestBody Map<String, Object> request) {
        try {
            String uniqueLink = (String) request.get("uniqueLink");
            Integer candidateId = (Integer) request.get("candidateId");
            Integer sessionId = (Integer) request.get("sessionId");

            QuizAttempt attempt = quizAttemptService.startQuizAttempt(uniqueLink, candidateId, sessionId);
            return ResponseEntity.ok(Map.of(
                "attemptId", attempt.getAttemptId(),
                "startTime", attempt.getStartTime(),
                "message", "Quiz attempt started successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{attemptId}/questions")
    public ResponseEntity<?> getQuizQuestions(@PathVariable Integer attemptId,
                                            @RequestParam Integer candidateId) {
        try {
            List<QuizQuestion> questions = quizAttemptService.getQuizQuestions(attemptId, candidateId);
            List<Boolean> questionStatus = quizAttemptService.getQuestionStatus(attemptId, candidateId);

            return ResponseEntity.ok(Map.of(
                "questions", questions,
                "questionStatus", questionStatus,
                "totalQuestions", questions.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{attemptId}/question/{questionIndex}")
    public ResponseEntity<?> getQuestion(@PathVariable Integer attemptId,
                                       @PathVariable Integer questionIndex,
                                       @RequestParam Integer candidateId) {
        try {
            // First validate that the attempt belongs to the candidate
            Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
            if (attemptOpt.isEmpty()) {
                return ResponseEntity.status(403).body(Map.of("error", "Invalid attempt or unauthorized access"));
            }

            QuizQuestion quizQuestion = quizAttemptService.getQuestionByIndex(attemptId, candidateId, questionIndex);
            Question question = quizQuestion.getQuestion();

            // Get options for the question - using service method
            List<QuestionOption> options = quizAttemptService.getQuestionOptions(question.getQuestionId(), attemptId);

            // Check if question is already answered
            Optional<AttemptAnswer> existingAnswer = attemptAnswerRepository.findByAttempt_AttemptIdAndQuestion_QuestionId(attemptId, question.getQuestionId());

            Integer selectedOptionId = null;
            if (existingAnswer.isPresent() && existingAnswer.get().getSelectedOption() != null) {
                selectedOptionId = existingAnswer.get().getSelectedOption().getOptionId();
            }

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("question", question);
            response.put("options", options);
            response.put("marks", quizQuestion.getMarks());
            response.put("questionIndex", questionIndex);
            response.put("isAnswered", existingAnswer.isPresent());
            response.put("selectedOptionId", selectedOptionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{attemptId}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable Integer attemptId,
                                        @RequestBody Map<String, Object> request) {
        try {
            Integer candidateId = (Integer) request.get("candidateId");
            Integer questionId = (Integer) request.get("questionId");
            Integer selectedOptionId = (Integer) request.get("selectedOptionId");

            AttemptAnswer answer = quizAttemptService.submitAnswer(attemptId, candidateId, questionId, selectedOptionId);
            return ResponseEntity.ok(Map.of(
                "answerId", answer.getAnswerId(),
                "isCorrect", answer.getIsCorrect(),
                "marksObtained", answer.getMarksObtained(),
                "message", "Answer submitted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Integer attemptId,
                                      @RequestBody Map<String, Object> request) {
        try {
            Integer candidateId = (Integer) request.get("candidateId");

            QuizAttempt attempt = quizAttemptService.submitQuiz(attemptId, candidateId);

            // Calculate and save scores
            scoringService.calculateAndSaveScores(attemptId);

            return ResponseEntity.ok(Map.of(
                "attemptId", attempt.getAttemptId(),
                "totalScore", attempt.getTotalScore(),
                "endTime", attempt.getEndTime(),
                "message", "Quiz submitted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{attemptId}/auto-submit")
    public ResponseEntity<?> autoSubmitQuiz(@PathVariable Integer attemptId) {
        try {
            QuizAttempt attempt = quizAttemptService.autoSubmitQuiz(attemptId);

            // Calculate and save scores
            scoringService.calculateAndSaveScores(attemptId);

            return ResponseEntity.ok(Map.of(
                "attemptId", attempt.getAttemptId(),
                "totalScore", attempt.getTotalScore(),
                "endTime", attempt.getEndTime(),
                "autoSubmitted", attempt.getAutoSubmitted(),
                "message", "Quiz auto-submitted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{attemptId}/status")
    public ResponseEntity<?> getAttemptStatus(@PathVariable Integer attemptId,
                                            @RequestParam Integer candidateId) {
        try {
            // Validate attempt belongs to candidate
            Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
            if (attemptOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid attempt or unauthorized access"));
            }

            QuizAttempt attempt = attemptOpt.get();
            Quiz quiz = attempt.getAssignment().getQuiz();

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("attemptId", attempt.getAttemptId());
            response.put("startTime", attempt.getStartTime());
            response.put("endTime", attempt.getEndTime());
            response.put("durationMinutes", quiz.getDurationMinutes());
            response.put("status", attempt.getEndTime() != null ? "Completed" : "In Progress");
            response.put("autoSubmitted", attempt.getAutoSubmitted());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}