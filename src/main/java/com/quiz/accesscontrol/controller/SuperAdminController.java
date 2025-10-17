package com.quiz.accesscontrol.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.quiz.accesscontrol.config.JwtUtil;
import com.quiz.accesscontrol.constants.Constant;
import com.quiz.accesscontrol.dto.UserCreateRequestDTO;
import com.quiz.accesscontrol.entity.MagicLink;
import com.quiz.accesscontrol.service.AuthService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // ================== SUPERADMIN ONLY ACTIONS ==================

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequestDTO request) {
        if (!request.getRole().equalsIgnoreCase(Constant.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body("Only SYSTEM can create SUPERADMIN");
        }
        Map<String, String> response = authService.createSuperAdmin(request, "SYSTEM");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invite-admin")
    public ResponseEntity<?> inviteAdmin(@RequestBody Map<String, String> body,
                                         @RequestHeader("Authorization") String token) throws JsonProcessingException {
        String superAdminEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        authService.checkSuperAdminAccess(superAdminEmail);
     
        String email = body.get("email");
        String fullName = body.get("fullName");

        // Call service → returns generated magic link
        String magicLink = authService.inviteAdmin(email, fullName, superAdminEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin invited successfully");
        response.put("magicLink", magicLink);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-admin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        String superAdminEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        authService.checkSuperAdminAccess(superAdminEmail); // ensure SUPERADMIN only
        authService.softDeleteAdmin(id, superAdminEmail);
        return ResponseEntity.ok("Admin soft-deleted");
    }

    @PutMapping("/restore-admin/{id}")
    public ResponseEntity<?> restoreAdmin(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        String superAdminEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        authService.checkSuperAdminAccess(superAdminEmail); // ensure SUPERADMIN only
        authService.restoreAdmin(id, superAdminEmail);
        return ResponseEntity.ok("Admin restored");
    }

    @PostMapping("/send-admin-forgot-link")
    public ResponseEntity<?> sendAdminForgotPasswordLink(@RequestBody Map<String, String> body,
                                                         @RequestHeader("Authorization") String token) {
        String superAdminEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        authService.checkSuperAdminAccess(superAdminEmail); // ensure SUPERADMIN only
        MagicLink magicLink = authService.generateAdminForgotPasswordLink(
                body.get("adminEmail"),
                superAdminEmail,
                "127.0.0.1"
        );
        return ResponseEntity.ok(Map.of(
                "adminEmail", body.get("adminEmail"),
                "magicToken", magicLink.getTokenHash(),
                "expiresAt", magicLink.getExpiresAt().toString()
        ));
    }

}
