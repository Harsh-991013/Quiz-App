package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByQuiz_QuizIdOrderById(Integer quizId);

    @Query("SELECT qq FROM QuizQuestion qq WHERE qq.quiz.quizId = :quizId ORDER BY qq.id")
    List<QuizQuestion> findQuestionsByQuizId(@Param("quizId") Integer quizId);
}