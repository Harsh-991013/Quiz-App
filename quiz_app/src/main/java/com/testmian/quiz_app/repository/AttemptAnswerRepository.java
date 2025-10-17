package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Integer> {
    List<AttemptAnswer> findByAttempt_AttemptId(Integer attemptId);
    Optional<AttemptAnswer> findByAttempt_AttemptIdAndQuestion_QuestionId(Integer attemptId, Integer questionId);
}