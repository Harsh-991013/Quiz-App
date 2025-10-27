package com.example.quiz.repository;

import com.example.quiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (active or inactive, but usually used for login check)
    Optional<User> findByEmail(String email);

    // ===================== ACTIVE USERS =====================

    // All users not soft-deleted
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();

    // All active users with status = Active
    @Query("SELECT u FROM User u WHERE u.status = 'Active' AND u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    // All active users by role
    @Query("SELECT u FROM User u WHERE u.role.roleId = :roleId AND u.deletedAt IS NULL")
    List<User> findAllActiveByRole(@Param("roleId") Long roleId);

    // Pending invitations (active only)
    @Query("SELECT u FROM User u WHERE u.inviteStatus = 'Pending' AND u.deletedAt IS NULL")
    List<User> findAllPendingInvitations();

    // ===================== ALL USERS =====================

    // All users including soft-deleted
    @Query("SELECT u FROM User u")
    List<User> findAllIncludingDeleted();

    // Users by role including soft-deleted
    @Query("SELECT u FROM User u WHERE u.role.roleId = :roleId")
    List<User> findAllByRoleIncludingDeleted(@Param("roleId") Long roleId);

    // Active or inactive users by status
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findAllByStatus(@Param("status") User.UserStatus status);
}

