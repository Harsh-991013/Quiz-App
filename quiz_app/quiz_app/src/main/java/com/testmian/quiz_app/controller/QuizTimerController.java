package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.service.QuizTimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quiz-timer")
@RequiredArgsConstructor
public class QuizTimerController {

    private final QuizTimerService quizTimerService;

    @GetMapping("/{attemptId}/remaining-time")
    public ResponseEntity<?> getRemainingTime(@PathVariable Integer attemptId,
                                            @RequestParam Integer candidateId) {
        try {
            long remainingMinutes = quizTimerService.getRemainingTimeMinutes(attemptId);
            long elapsedMinutes = quizTimerService.getElapsedTimeMinutes(attemptId);
            boolean hasTimedOut = quizTimerService.hasTimedOut(attemptId);

            return ResponseEntity.ok(Map.of(
                "remainingMinutes", remainingMinutes,
                "elapsedMinutes", elapsedMinutes,
                "hasTimedOut", hasTimedOut,
                "message", hasTimedOut ? "Quiz has timed out" : "Quiz is still active"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{attemptId}/status")
    public ResponseEntity<?> getTimerStatus(@PathVariable Integer attemptId,
                                          @RequestParam Integer candidateId) {
        try {
            long remainingMinutes = quizTimerService.getRemainingTimeMinutes(attemptId);
            long elapsedMinutes = quizTimerService.getElapsedTimeMinutes(attemptId);
            boolean hasTimedOut = quizTimerService.hasTimedOut(attemptId);

            String status;
            if (hasTimedOut) {
                status = "TIMED_OUT";
            } else if (remainingMinutes <= 5) {
                status = "CRITICAL";
            } else if (remainingMinutes <= 10) {
                status = "WARNING";
            } else {
                status = "NORMAL";
            }

            return ResponseEntity.ok(Map.of(
                "status", status,
                "remainingMinutes", remainingMinutes,
                "elapsedMinutes", elapsedMinutes,
                "hasTimedOut", hasTimedOut
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}