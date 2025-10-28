package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.entity.QuestionType;
import com.testmian.quiz_app.service.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/question-types")
public class QuestionTypeController {

    @Autowired
    private QuestionTypeService questionTypeService;

    @GetMapping
    public ResponseEntity<List<QuestionType>> getAll() {
        return ResponseEntity.ok(questionTypeService.getAll());
    }

    @PostMapping
    public ResponseEntity<QuestionType> create(@RequestBody QuestionType type) {
        return ResponseEntity.ok(questionTypeService.create(type));
    }
    
}