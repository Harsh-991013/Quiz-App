package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.constants.Constant;
import com.testmian.quiz_app.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String superAdmin);
}
