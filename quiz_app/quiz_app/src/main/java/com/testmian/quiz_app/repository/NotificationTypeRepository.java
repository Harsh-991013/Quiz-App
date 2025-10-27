package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
    NotificationType findByTypeKey(String typeKey);
}