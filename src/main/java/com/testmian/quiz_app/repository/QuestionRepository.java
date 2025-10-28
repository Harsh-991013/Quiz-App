package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {


    boolean existsByQuestionText(String questionText);
}
