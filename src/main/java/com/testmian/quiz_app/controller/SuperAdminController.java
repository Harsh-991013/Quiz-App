package com.testmian.quiz_app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testmian.quiz_app.dto.InviteUserDto;
import com.testmian.quiz_app.dto.UserCreateRequestDTO;
import com.testmian.quiz_app.entity.MagicLink;
import com.testmian.quiz_app.service.AuthService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

    @Autowired
    private AuthService authService;
    

    // ================== SUPERADMIN ONLY ACTIONS ==================

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequestDTO request) {
    	
        if (!request.getRole().equalsIgnoreCase("SUPERADMIN")) {
            return ResponseEntity.status(403).body("Only SYSTEM can create SUPERADMIN");
        }
        Map<String, String> response = authService.createSuperAdmin(request, "SYSTEM");
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/invite-admin")
    public ResponseEntity<?> inviteAdmin(@Valid @RequestBody InviteUserDto user,
    		@RequestHeader(value = "Authorization", required = false) String token) throws JsonProcessingException {
    	
    	 String superAdminEmail = authService.validateAndGetSuperAdmin(token);
       
        String email = user.getEmail();
        String fullName = user.getFullName();
        
        String magicLink = authService.inviteAdmin(email, fullName, superAdminEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin invited successfully");
        response.put("magicLink", magicLink);
        
        return ResponseEntity.ok(response);
    }
    

    @DeleteMapping("/delete-admin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
    	String superAdminEmail = authService.validateAndGetSuperAdmin(token);
        authService.softDeleteAdmin(id, superAdminEmail);
        return ResponseEntity.ok("Admin soft-deleted");
    }

    @PutMapping("/restore-admin/{id}")
    public ResponseEntity<?> restoreAdmin(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
    	 String superAdminEmail = authService.validateAndGetSuperAdmin(token);
        authService.restoreAdmin(id, superAdminEmail);
        return ResponseEntity.ok("Admin restored");
    }

    @PostMapping("/send-admin-forgot-link")
    public ResponseEntity<?> sendAdminForgotPasswordLink(@RequestBody Map<String, String> body,
                                                         @RequestHeader("Authorization") String token) {
    	 String superAdminEmail = authService.validateAndGetSuperAdmin(token);
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
