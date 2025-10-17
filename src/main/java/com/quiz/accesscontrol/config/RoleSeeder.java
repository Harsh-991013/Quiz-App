package com.quiz.accesscontrol.config;

import com.quiz.accesscontrol.entity.Role;
import com.quiz.accesscontrol.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class RoleSeeder implements CommandLineRunner {

    @Autowired 
    private RoleRepository roleRepo;

    @Override
    public void run(String... args) {
        if (roleRepo.count() == 0) {
            roleRepo.save(new Role("SUPERADMIN", "System-level access", "system"));
            roleRepo.save(new Role("ADMIN", "Manages platform operations", "system"));
            roleRepo.save(new Role("CANDIDATE", "Takes quizzes and assessments", "system"));
        }
    }
}
