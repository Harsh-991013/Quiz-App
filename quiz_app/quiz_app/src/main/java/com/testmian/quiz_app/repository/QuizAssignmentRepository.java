package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizAssignmentRepository extends JpaRepository<QuizAssignment, Integer> {
    Optional<QuizAssignment> findByUniqueLink(String uniqueLink);
    Optional<QuizAssignment> findByAssignmentIdAndCandidate_UserId(Integer assignmentId, Integer candidateId);
}