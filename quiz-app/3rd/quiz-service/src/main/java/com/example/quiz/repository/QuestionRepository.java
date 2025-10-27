package com.example.quiz.repository;

import com.example.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.deletedAt IS NULL AND q.isActive = true")
    List<Question> findAllActive();

    @Query("SELECT q FROM Question q WHERE q.deletedAt IS NULL AND q.isActive = true AND q.category.categoryId = :categoryId")
    List<Question> findAllActiveByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT q FROM Question q WHERE q.deletedAt IS NULL AND q.isActive = true AND q.difficulty.difficultyId = :difficultyId")
    List<Question> findAllActiveByDifficulty(@Param("difficultyId") Long difficultyId);

    @Query("SELECT q FROM Question q WHERE q.deletedAt IS NULL AND q.isActive = true AND q.questionType.questionTypeId = :questionTypeId")
    List<Question> findAllActiveByQuestionType(@Param("questionTypeId") Long questionTypeId);

    @Query("SELECT q FROM Question q WHERE q.questionId IN :questionIds AND q.deletedAt IS NULL AND q.isActive = true")
    List<Question> findAllActiveByIds(@Param("questionIds") List<Long> questionIds);
}