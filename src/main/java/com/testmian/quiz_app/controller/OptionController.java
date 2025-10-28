package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.entity.Option;
import com.testmian.quiz_app.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    @Autowired
    private OptionService optionService;

    //  Get all active (non-deleted) options for a question
    @GetMapping("/{questionId}")
    public List<Option> getOptionsByQuestion(@PathVariable Integer questionId) {
        return optionService.getOptionsByQuestion(questionId);
    }

    // Create option for a question
    @PostMapping("/{questionId}")
    public ResponseEntity<Option> createOption(@PathVariable Integer questionId, @RequestBody Option option) {
        Option created = optionService.createOption(questionId, option);
        return ResponseEntity.ok(created);
    }

    //  Update an existing option
    @PutMapping("/{id}")
    public ResponseEntity<Option> updateOption(@PathVariable Integer id, @RequestBody Option updated) {
        Option updatedOption = optionService.updateOption(id, updated);
        return ResponseEntity.ok(updatedOption);
    }

    //  Soft delete an option
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOption(@PathVariable Integer id) {
        optionService.deleteOption(id);
        return ResponseEntity.ok("Option soft-deleted successfully.");
    }

    // Restore a soft-deleted option
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Option> restoreOption(@PathVariable Integer id) {
        Option restored = optionService.restoreOption(id);
        return ResponseEntity.ok(restored);
    }
}
