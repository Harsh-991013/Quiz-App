package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Integer> {
    DeliveryStatus findByStatusKey(String statusKey);
}