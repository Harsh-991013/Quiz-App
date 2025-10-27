package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Integer> {
    List<QuestionOption> findByQuestion_QuestionIdOrderByOptionId(Integer questionId);
}