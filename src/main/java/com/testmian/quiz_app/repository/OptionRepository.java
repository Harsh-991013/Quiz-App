package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.Option;
import com.testmian.quiz_app.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {
    List<Option> findByQuestion(Question question);
}
