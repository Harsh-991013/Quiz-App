package com.testmian.quiz_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.DeliveryStatus;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {
    Optional<DeliveryStatus> findByStatusKey(String statusKey);
}
