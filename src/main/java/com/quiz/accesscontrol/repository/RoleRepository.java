package com.quiz.accesscontrol.repository;

import com.quiz.accesscontrol.constants.Constant;
import com.quiz.accesscontrol.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String superAdmin);
}
