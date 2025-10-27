package com.example.quiz.repository;

import com.example.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NULL AND q.isActive = true")
    List<Quiz> findAllActive();

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NULL AND q.isActive = true AND q.createdBy.userId = :creatorId")
    List<Quiz> findAllActiveByCreator(@Param("creatorId") Long creatorId);

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NULL AND q.startTime <= :now AND q.endTime >= :now")
    List<Quiz> findActiveQuizzesInTimeRange(@Param("now") LocalDateTime now);

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NOT NULL")
    List<Quiz> findAllDeleted();

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NOT NULL AND q.createdBy.userId = :creatorId")
    List<Quiz> findAllDeletedByCreator(@Param("creatorId") Long creatorId);
}