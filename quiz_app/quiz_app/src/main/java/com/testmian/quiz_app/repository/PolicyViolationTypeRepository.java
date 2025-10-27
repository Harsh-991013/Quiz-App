package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.PolicyViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyViolationTypeRepository extends JpaRepository<PolicyViolationType, Integer> {
    PolicyViolationType findByViolationKey(String violationKey);
}