package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.entity.Score;
import com.testmian.quiz_app.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<?> getScoresByAttempt(@PathVariable Integer attemptId) {
        try {
            List<Score> scores = scoreService.getScoresByAttemptId(attemptId);
            return ResponseEntity.ok(Map.of(
                "attemptId", attemptId,
                "scores", scores,
                "totalScore", scores.stream().mapToDouble(Score::getScore).sum()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getScoresByCandidate(@PathVariable Integer candidateId) {
        try {
            List<Score> scores = scoreService.getScoresByCandidateId(candidateId);
            return ResponseEntity.ok(Map.of(
                "candidateId", candidateId,
                "scores", scores,
                "totalQuizzes", scores.size(),
                "averageScore", scores.stream().mapToDouble(Score::getScore).average().orElse(0.0)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<?> getScoresByQuiz(@PathVariable Integer quizId) {
        try {
            List<Score> scores = scoreService.getScoresByQuizId(quizId);
            return ResponseEntity.ok(Map.of(
                "quizId", quizId,
                "scores", scores,
                "totalCandidates", scores.size(),
                "averageScore", scores.stream().mapToDouble(Score::getScore).average().orElse(0.0)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}