package com.example.quiz.service;

import com.example.quiz.entity.MagicLink;
import com.example.quiz.entity.Role;
import com.example.quiz.entity.User;
import com.example.quiz.repository.RoleRepository;
import com.example.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MagicLinkService magicLinkService;

    @Transactional
    public User registerUser(String email, String fullName, String roleName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + roleName));

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setAuthMethod(User.AuthMethod.magic_link);
        user.setStatus(User.UserStatus.Inactive);

        User savedUser = userRepository.save(user);

        // Generate activation magic link
        magicLinkService.generateMagicLink(savedUser, MagicLink.Purpose.ACTIVATE_ADMIN);

        log.info("User registered successfully: {}", email);
        return savedUser;
    }
}
