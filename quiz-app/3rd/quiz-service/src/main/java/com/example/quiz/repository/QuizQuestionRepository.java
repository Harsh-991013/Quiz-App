package com.example.quiz.repository;

import com.example.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.quiz.quizId = :quizId ORDER BY qq.questionOrder")
    List<QuizQuestion> findByQuizIdOrderByQuestionOrder(@Param("quizId") Long quizId);

    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.question.questionId = :questionId")
    List<QuizQuestion> findByQuestionId(@Param("questionId") Long questionId);
}