package com.example.quiz.repository;

import com.example.quiz.entity.QuizAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAssignmentRepository extends JpaRepository<QuizAssignment, Long> {

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.deletedAt IS NULL")
    List<QuizAssignment> findAllActive();

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.quiz.quizId = :quizId AND qa.deletedAt IS NULL")
    List<QuizAssignment> findAllActiveByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.candidate.userId = :candidateId AND qa.deletedAt IS NULL")
    List<QuizAssignment> findAllActiveByCandidateId(@Param("candidateId") Long candidateId);

    Optional<QuizAssignment> findByUniqueLink(String uniqueLink);

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.expiresAt < :now AND qa.status = 'Assigned' AND qa.deletedAt IS NULL")
    List<QuizAssignment> findExpiredAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.quiz.quizId = :quizId AND qa.candidate.userId = :candidateId AND qa.deletedAt IS NULL")
    List<QuizAssignment> findAllActiveByQuizIdAndCandidateId(@Param("quizId") Long quizId, @Param("candidateId") Long candidateId);

    @Query("SELECT qa FROM QuizAssignment qa WHERE qa.quiz.quizId = :quizId AND qa.candidate.userId = :candidateId AND qa.deletedAt IS NULL ORDER BY qa.assignedAt DESC LIMIT 1")
    Optional<QuizAssignment> findLatestActiveAssignmentForQuizAndCandidate(@Param("quizId") Long quizId, @Param("candidateId") Long candidateId);
}