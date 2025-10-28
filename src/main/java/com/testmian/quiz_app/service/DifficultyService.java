package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.Difficulty;
import com.testmian.quiz_app.repository.DifficultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DifficultyService {

    @Autowired
    private DifficultyRepository difficultyRepository;

    public List<Difficulty> getAll() {
        return difficultyRepository.findAll();
    }

    public Difficulty create(Difficulty difficulty) {
        if (difficultyRepository.existsByDifficultyKey(difficulty.getDifficultyKey())) {
            throw new RuntimeException("Difficulty already exists.");
        }
        return difficultyRepository.save(difficulty);
    }
}