package com.quiz.accesscontrol.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quiz.accesscontrol.entity.Session;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
	@Query("SELECT s FROM Session s WHERE s.user.userId = :userId AND s.isActive = true")
	List<Session> findActiveSessionsByUserId(@Param("userId") Long userId);

}