package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.service.QuizMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quiz-monitoring")
@RequiredArgsConstructor
public class QuizMonitoringController {

    private final QuizMonitoringService quizMonitoringService;
    private final com.testmian.quiz_app.repository.QuizAttemptRepository quizAttemptRepository;

    @PostMapping("/violation")
    public ResponseEntity<?> recordViolation(@RequestBody Map<String, Object> request) {
        try {
            Integer attemptId = (Integer) request.get("attemptId");
            Integer candidateId = (Integer) request.get("candidateId");
            String violationKey = (String) request.get("violationType");
            String description = (String) request.get("description");
            String ipAddress = (String) request.get("ipAddress");
            String deviceInfo = (String) request.get("deviceInfo");

            System.out.println("DEBUG: Recording violation for attemptId=" + attemptId + ", candidateId=" + candidateId);

            // First validate that the attempt belongs to the candidate
            var attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
            System.out.println("DEBUG: Attempt validation result: " + (attemptOpt.isPresent() ? "FOUND" : "NOT FOUND"));

            if (attemptOpt.isEmpty()) {
                System.out.println("DEBUG: Authorization failed - returning 403");
                return ResponseEntity.status(403).body(Map.of("error", "Invalid attempt or unauthorized access"));
            }

            System.out.println("DEBUG: Authorization passed - recording violation");
            quizMonitoringService.recordViolation(attemptId, candidateId, violationKey, description, ipAddress, deviceInfo);

            return ResponseEntity.ok(Map.of(
                "message", "Violation recorded successfully",
                "violationType", violationKey
            ));
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in recordViolation: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/violations/{attemptId}")
    public ResponseEntity<?> getViolations(@PathVariable Integer attemptId,
                                         @RequestParam Integer candidateId) {
        try {
            var violations = quizMonitoringService.getViolationsForAttempt(attemptId);
            var warnings = quizMonitoringService.getWarningsForAttempt(attemptId);

            return ResponseEntity.ok(Map.of(
                "violations", violations,
                "warnings", warnings,
                "violationCount", quizMonitoringService.getViolationCountForAttempt(attemptId),
                "warningCount", quizMonitoringService.getWarningCountForAttempt(attemptId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{attemptId}")
    public ResponseEntity<?> getMonitoringStatus(@PathVariable Integer attemptId,
                                               @RequestParam Integer candidateId) {
        try {
            int violationCount = quizMonitoringService.getViolationCountForAttempt(attemptId);
            int warningCount = quizMonitoringService.getWarningCountForAttempt(attemptId);

            // Determine status based on counts
            String status = "NORMAL";
            if (warningCount > 0) {
                status = "WARNING";
            }
            if (violationCount >= 3) { // Assuming 3 violations trigger auto-submit
                status = "CRITICAL";
            }

            return ResponseEntity.ok(Map.of(
                "status", status,
                "violationCount", violationCount,
                "warningCount", warningCount,
                "message", getStatusMessage(status, warningCount)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getStatusMessage(String status, int warningCount) {
        switch (status) {
            case "NORMAL":
                return "No violations detected";
            case "WARNING":
                return warningCount + " warning(s) issued";
            case "CRITICAL":
                return "Critical violations detected - auto-submit may occur";
            default:
                return "Unknown status";
        }
    }
}