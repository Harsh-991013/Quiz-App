package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.ViolationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationActionRepository extends JpaRepository<ViolationAction, Integer> {
    ViolationAction findByActionKey(String actionKey);
}