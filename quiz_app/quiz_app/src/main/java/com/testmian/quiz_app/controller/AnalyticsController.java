package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/quiz/{quizId}/summary")
    public ResponseEntity<?> getQuizSummary(@PathVariable Integer quizId) {
        try {
            Map<String, Object> summary = analyticsService.getQuizSummary(quizId);
            return ResponseEntity.ok(Map.of(
                "quizId", quizId,
                "summary", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/candidate/{candidateId}/performance")
    public ResponseEntity<?> getCandidatePerformance(@PathVariable Integer candidateId) {
        try {
            Map<String, Object> performance = analyticsService.getCandidatePerformance(candidateId);
            return ResponseEntity.ok(Map.of(
                "candidateId", candidateId,
                "performance", performance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/question/{questionId}/stats")
    public ResponseEntity<?> getQuestionStats(@PathVariable Integer questionId) {
        try {
            Map<String, Object> stats = analyticsService.getQuestionStats(questionId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/overview")
    public ResponseEntity<?> getDashboardOverview() {
        try {
            Map<String, Object> overview = analyticsService.getDashboardOverview();
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/latest-test-records")
    public ResponseEntity<?> getLatestTestRecords() {
        try {
            List<Map<String, Object>> records = analyticsService.getLatestTestRecords();
            return ResponseEntity.ok(Map.of(
                "records", records,
                "totalRecords", records.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}