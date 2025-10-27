package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.AutoSubmitReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoSubmitReasonRepository extends JpaRepository<AutoSubmitReason, Integer> {
    AutoSubmitReason findByReasonKey(String reasonKey);
}