package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuizPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizPolicyRepository extends JpaRepository<QuizPolicy, Integer> {
    QuizPolicy findByQuiz_QuizId(Integer quizId);
}