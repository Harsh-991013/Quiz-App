package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.dto.ImportResult;
import com.testmian.quiz_app.entity.Question;
import com.testmian.quiz_app.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Get all active (non-deleted) questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllActiveQuestions();
    }

    // Get a single question by ID
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new question
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question created = questionService.createQuestion(question);
        return ResponseEntity.ok(created);
    }

    // Update existing question
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Integer id, @RequestBody Question updated) {
        Question updatedQuestion = questionService.updateQuestion(id, updated);
        return ResponseEntity.ok(updatedQuestion);
    }

    // Soft delete (mark deleted + inactive)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Integer id) {
        questionService.softDeleteQuestion(id);
        return ResponseEntity.ok("Question deleted successfully.");
    }

    // Restore soft-deleted question
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Question> restoreQuestion(@PathVariable Integer id) {
        Question restored = questionService.restoreQuestion(id);
        return ResponseEntity.ok(restored);
    }

    /**
     * Imports questions from an Excel file.
     * Returns structured validation results: success count, failed count, and row-level errors.
     */
    @PostMapping("/import")
    public ResponseEntity<?> importQuestions(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        ImportResult result = questionService.importQuestionsFromExcel(file);
        return ResponseEntity.ok(result);
    }
}
