package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuestionTypeRepository extends JpaRepository<QuestionType, Integer> {
    Optional<QuestionType> findByTypeKey(String typeKey);
    boolean existsByTypeKey(String typeKey);
}
