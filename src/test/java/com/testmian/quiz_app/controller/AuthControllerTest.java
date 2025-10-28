package com.testmian.quiz_app.controller;

import com.testmian.quiz_app.controller.AuthController;
import com.testmian.quiz_app.dto.AcceptInviteResponseDTO;
import com.testmian.quiz_app.dto.LoginRequestDTO;
import com.testmian.quiz_app.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        request.setDeviceInfo("Chrome");
        request.setIpAddress("127.0.0.1");

        String mockedToken = "mocked-jwt-token";

        when(authService.login(
                request.getEmail(),
                request.getPassword(),
                request.getDeviceInfo(),
                request.getIpAddress()
        )).thenReturn(mockedToken);

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> expected = Map.of("token", mockedToken);
        assertEquals(expected, response.getBody());

        verify(authService, times(1))
                .login(request.getEmail(), request.getPassword(), request.getDeviceInfo(), request.getIpAddress());
    }

    @Test
    void testAcceptInvite_Success() {
        // Arrange
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "invitee@example.com");
        requestBody.put("password", "invitePassword");

        AcceptInviteResponseDTO mockResponse = new AcceptInviteResponseDTO();
        mockResponse.setMessage("Invite accepted");

        when(authService.acceptInvite("invitee@example.com", "invitePassword")).thenReturn(mockResponse);

        // Act
        ResponseEntity<AcceptInviteResponseDTO> response = authController.acceptInvite(requestBody);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());

        verify(authService, times(1)).acceptInvite("invitee@example.com", "invitePassword");
    }

    @Test
    void testResetAdminPassword_Success() {
        // Arrange
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("magicToken", "magic-token-123");
        requestBody.put("newPassword", "newPassword123");

        doNothing().when(authService).resetAdminPassword("magic-token-123", "newPassword123");

        // Act
        ResponseEntity<?> response = authController.resetAdminPassword(requestBody);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password updated successfully", response.getBody());

        verify(authService, times(1)).resetAdminPassword("magic-token-123", "newPassword123");
    }
}
