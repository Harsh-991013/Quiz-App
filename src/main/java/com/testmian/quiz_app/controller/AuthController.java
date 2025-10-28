package com.testmian.quiz_app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testmian.quiz_app.dto.AcceptInviteResponseDTO;
import com.testmian.quiz_app.dto.LoginRequestDTO;
import com.testmian.quiz_app.service.AuthService;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired 
    private AuthService authService;




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        String token = authService.login(
            loginRequest.getEmail(),
            loginRequest.getPassword(),
            loginRequest.getDeviceInfo(),
            loginRequest.getIpAddress()
        );
        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/accept-invite")
    public ResponseEntity<AcceptInviteResponseDTO> acceptInvite(@RequestBody Map<String, String> body) {
        AcceptInviteResponseDTO response = authService.acceptInvite(
                body.get("email"),
                body.get("password")
        );
        return ResponseEntity.ok(response);
    }



 
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetAdminPassword(@RequestBody Map<String, String> body) {
        String token = body.get("magicToken");
        String newPassword = body.get("newPassword");
        authService.resetAdminPassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

   
}
