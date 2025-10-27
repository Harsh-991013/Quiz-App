package com.example.quiz.controller;

import com.example.quiz.entity.QuizAssignment;
import com.example.quiz.entity.User;
import com.example.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final QuizService quizService;

    @GetMapping("/assignments")
    public ResponseEntity<List<QuizAssignment>> getMyAssignments(@AuthenticationPrincipal User user) {
        List<QuizAssignment> assignments = quizService.getAssignmentsByCandidate(user.getUserId());
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/assignments/{uniqueLink}")
    public ResponseEntity<QuizAssignment> getAssignmentByLink(@PathVariable String uniqueLink) {
        QuizAssignment assignment = quizService.getAssignmentByUniqueLink(uniqueLink);
        return ResponseEntity.ok(assignment);
    }
}