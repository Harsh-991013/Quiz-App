package com.quiz.accesscontrol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.accesscontrol.entity.DeliveryStatus;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {
    Optional<DeliveryStatus> findByStatusKey(String statusKey);
}
