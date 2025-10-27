package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizWarningRepository extends JpaRepository<QuizWarning, Integer> {
    List<QuizWarning> findByAttempt_AttemptId(Integer attemptId);
    long countByAttempt_AttemptId(Integer attemptId);
}