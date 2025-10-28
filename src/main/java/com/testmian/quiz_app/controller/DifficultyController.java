package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.entity.Difficulty;
import com.testmian.quiz_app.service.DifficultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/difficulties")
public class DifficultyController {

    @Autowired
    private DifficultyService difficultyService;

    @GetMapping
    public ResponseEntity<List<Difficulty>> getAll() {
        return ResponseEntity.ok(difficultyService.getAll());
    }

    @PostMapping
    public ResponseEntity<Difficulty> create(@RequestBody Difficulty difficulty) {
        return ResponseEntity.ok(difficultyService.create(difficulty));
    }

}
