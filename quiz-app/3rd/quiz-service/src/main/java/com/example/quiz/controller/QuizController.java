package com.example.quiz.controller;

import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizAssignment;
import com.example.quiz.entity.User;
import com.example.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<Quiz> createQuiz(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) Long difficultyId,
            @RequestParam List<Long> questionIds,
            @AuthenticationPrincipal User user) {

        Quiz quiz = quizService.createQuiz(title, description, startTime, endTime,
                durationMinutes, difficultyId, questionIds, user);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<Quiz> updateQuiz(
            @PathVariable Long quizId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) Integer durationMinutes,
            @AuthenticationPrincipal User user) {

        Quiz quiz = quizService.updateQuiz(quizId, title, description, startTime,
                endTime, durationMinutes, user);
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<Void> softDeleteQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User user) {

        quizService.softDeleteQuiz(quizId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/restore")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> restoreQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User user) {

        quizService.restoreQuiz(quizId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllActiveQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<List<Quiz>> getMyQuizzes(@AuthenticationPrincipal User user) {
        List<Quiz> quizzes = quizService.getQuizzesByCreator(user.getUserId());
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long quizId) {
        Quiz quiz = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/{quizId}/assign/{candidateId}")
    public ResponseEntity<String> assignQuizToCandidate(
            @PathVariable Long quizId,
            @PathVariable Long candidateId) {

        String result = quizService.assignQuizToCandidate(quizId, candidateId, null);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{quizId}/assignments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<List<QuizAssignment>> getQuizAssignments(@PathVariable Long quizId) {
        List<QuizAssignment> assignments = quizService.getAssignmentsByQuiz(quizId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/assignments/my")
    public ResponseEntity<List<QuizAssignment>> getMyAssignments(@AuthenticationPrincipal User user) {
        List<QuizAssignment> assignments = quizService.getAssignmentsByCandidate(user.getUserId());
        return ResponseEntity.ok(assignments);
    }
}