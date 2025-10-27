package com.example.quiz.controller;

import com.example.quiz.entity.MagicLink;
import com.example.quiz.entity.Role;
import com.example.quiz.entity.User;
import com.example.quiz.repository.MagicLinkRepository;
import com.example.quiz.service.MagicLinkService;
import com.example.quiz.repository.RoleRepository;
import com.example.quiz.repository.UserRepository;
import com.example.quiz.service.EmailService;
import com.example.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import com.example.quiz.util.JwtUtil;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MagicLinkRepository magicLinkRepository;
    private final MagicLinkService magicLinkService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // 1️⃣ Register User
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // Set role based on request
        Optional<Role> roleOpt = roleRepository.findByRoleName(request.getRole().toUpperCase());
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid role specified");
        }
        user.setRole(roleOpt.get());

        user.setStatus(User.UserStatus.Active);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // DTO for registration request
    public static class RegisterRequest {
        private String email;
        private String fullName;
        private String role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // 2️⃣ Request Magic Link
    @PostMapping("/magic-link/request")
    public ResponseEntity<?> requestMagicLink(@RequestBody MagicLinkRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

    // Use MagicLinkService to create, save (with hashing) and send the magic link email
    MagicLink saved = magicLinkService.generateMagicLink(user, MagicLink.Purpose.valueOf(request.getPurpose().toUpperCase()));

    return ResponseEntity.ok("Magic link sent to " + user.getEmail());
    }


    // 4️⃣ Validate Magic Link
    @PostMapping("/magic-link/validate")
    public ResponseEntity<?> validateMagicLink(@RequestBody ValidateTokenRequest request) {
        // Use MagicLinkService to validate raw token (it hashes internally)
        Optional<MagicLink> linkOpt = magicLinkService.validateMagicLink(request.getToken());
        if (linkOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid token");

        MagicLink link = linkOpt.get();
        if (link.getUsed()) return ResponseEntity.badRequest().body("Token already used");
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) return ResponseEntity.badRequest().body("Token expired");

        // Mark used and persist
        link.setUsed(true);
        magicLinkRepository.save(link);

        // Issue JWT (subject: email, role: ROLE_<ROLE>)
        String subject = link.getUser().getEmail();
        String roleName = link.getUser().getRole() != null ? link.getUser().getRole().getRoleName() : "USER";
        String role = "ROLE_" + roleName.toUpperCase();
        String jwt = jwtUtil.generateToken(subject, role);

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "expiresIn",  jwtUtil == null ? 0 : (jwtUtil != null ?  (long) (60*30) : 0),
                "email", link.getUser().getEmail()
        ));
    }

    // DTO for request
    public static class MagicLinkRequest {
        private String email;
        private String purpose;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }


    // DTO for token validation
    public static class ValidateTokenRequest {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
