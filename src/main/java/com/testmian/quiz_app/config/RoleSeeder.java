package com.testmian.quiz_app.config;

import com.testmian.quiz_app.entity.Role;
import com.testmian.quiz_app.repository.RoleRepository;

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
        	roleRepo.save(new Role("SUPERADMIN", "System-level access", 1));
        	roleRepo.save(new Role("ADMIN", "Manages platform operations", 1));
        	roleRepo.save(new Role("CANDIDATE", "Takes quizzes and assessments", 1));

        }
    }
}
