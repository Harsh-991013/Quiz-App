package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {
    Optional<QuizAttempt> findByAssignment_AssignmentId(Integer assignmentId);
    Optional<QuizAttempt> findByAttemptIdAndAssignment_Candidate_UserId(Integer attemptId, Integer candidateId);
}