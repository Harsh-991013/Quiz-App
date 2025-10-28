package com.testmian.quiz_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testmian.quiz_app.controller.SuperAdminController;
import com.testmian.quiz_app.dto.InviteUserDto;
import com.testmian.quiz_app.dto.UserCreateRequestDTO;
import com.testmian.quiz_app.entity.MagicLink;
import com.testmian.quiz_app.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SuperAdminControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private SuperAdminController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setRole("SUPERADMIN");
        request.setEmail("superadmin@example.com");
        request.setFullName("Super Admin");

        Map<String, String> mockResponse = Map.of("message", "SuperAdmin created successfully");

        when(authService.createSuperAdmin(request, "SYSTEM")).thenReturn(mockResponse);

        ResponseEntity<?> response = controller.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());

        verify(authService, times(1)).createSuperAdmin(request, "SYSTEM");
    }

    @Test
    void testCreateUser_ForbiddenRole() {
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setRole("ADMIN");

        ResponseEntity<?> response = controller.createUser(request);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Only SYSTEM can create SUPERADMIN", response.getBody());

        verify(authService, never()).createSuperAdmin(any(), any());
    }

    @Test
    void testInviteAdmin_Success() throws JsonProcessingException {
        InviteUserDto inviteUser = new InviteUserDto();
        inviteUser.setEmail("admin@example.com");
        inviteUser.setFullName("Admin User");

        String token = "Bearer mock-token";
        String superAdminEmail = "superadmin@example.com";
        String magicLink = "magic-link-token";

        when(authService.validateAndGetSuperAdmin(token)).thenReturn(superAdminEmail);
        when(authService.inviteAdmin("admin@example.com", "Admin User", superAdminEmail)).thenReturn(magicLink);

        ResponseEntity<?> response = controller.inviteAdmin(inviteUser, token);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> expected = Map.of(
                "message", "Admin invited successfully",
                "magicLink", magicLink
        );
        assertEquals(expected, response.getBody());

        verify(authService, times(1)).validateAndGetSuperAdmin(token);
        verify(authService, times(1)).inviteAdmin("admin@example.com", "Admin User", superAdminEmail);
    }

    @Test
    void testDeleteAdmin_Success() {
        String token = "Bearer mock-token";
        Long adminId = 1L;
        String superAdminEmail = "superadmin@example.com";

        when(authService.validateAndGetSuperAdmin(token)).thenReturn(superAdminEmail);
        doNothing().when(authService).softDeleteAdmin(adminId, superAdminEmail);

        ResponseEntity<?> response = controller.deleteAdmin(adminId, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin soft-deleted", response.getBody());

        verify(authService, times(1)).softDeleteAdmin(adminId, superAdminEmail);
    }

    @Test
    void testRestoreAdmin_Success() {
        String token = "Bearer mock-token";
        Long adminId = 1L;
        String superAdminEmail = "superadmin@example.com";

        when(authService.validateAndGetSuperAdmin(token)).thenReturn(superAdminEmail);
        doNothing().when(authService).restoreAdmin(adminId, superAdminEmail);

        ResponseEntity<?> response = controller.restoreAdmin(adminId, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin restored", response.getBody());

        verify(authService, times(1)).restoreAdmin(adminId, superAdminEmail);
    }

    @Test
    void testSendAdminForgotPasswordLink_Success() {
        String token = "Bearer mock-token";
        String superAdminEmail = "superadmin@example.com";
        String adminEmail = "admin@example.com";

        MagicLink magicLink = new MagicLink();
        magicLink.setTokenHash("magic-token-123");
        magicLink.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(authService.validateAndGetSuperAdmin(token)).thenReturn(superAdminEmail);
        when(authService.generateAdminForgotPasswordLink(adminEmail, superAdminEmail, "127.0.0.1"))
                .thenReturn(magicLink);

        Map<String, String> body = Map.of("adminEmail", adminEmail);

        ResponseEntity<?> response = controller.sendAdminForgotPasswordLink(body, token);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> expected = Map.of(
                "adminEmail", adminEmail,
                "magicToken", magicLink.getTokenHash(),
                "expiresAt", magicLink.getExpiresAt().toString()
        );
        assertEquals(expected, response.getBody());

        verify(authService, times(1)).generateAdminForgotPasswordLink(adminEmail, superAdminEmail, "127.0.0.1");
    }
}
