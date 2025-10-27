package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizViolation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizViolationRepository extends JpaRepository<QuizViolation, Integer> {
    List<QuizViolation> findByAttempt_AttemptId(Integer attemptId);
    long countByAttempt_AttemptId(Integer attemptId);
}