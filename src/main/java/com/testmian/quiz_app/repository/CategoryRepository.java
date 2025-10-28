package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // already used in QuestionService
    Optional<Category> findByCategoryName(String categoryName);

    //  new method to fix your error
    boolean existsByCategoryName(String categoryName);
}
