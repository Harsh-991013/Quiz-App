package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DifficultyRepository extends JpaRepository<Difficulty, Integer> {


    Optional<Difficulty> findByDifficultyKey(String difficultyKey);


    boolean existsByDifficultyKey(String difficultyKey);
}
