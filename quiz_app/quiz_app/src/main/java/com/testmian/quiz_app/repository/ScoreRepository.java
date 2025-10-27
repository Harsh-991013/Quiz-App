package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    List<Score> findByAttempt_AttemptId(Integer attemptId);
    List<Score> findByCandidate_UserId(Integer candidateId);
}