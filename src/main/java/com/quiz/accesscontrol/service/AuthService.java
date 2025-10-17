package com.quiz.accesscontrol.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.quiz.accesscontrol.dto.AcceptInviteResponseDTO;
import com.quiz.accesscontrol.dto.EmailEvent;
import com.quiz.accesscontrol.dto.UserCreateRequestDTO;
import com.quiz.accesscontrol.entity.MagicLink;
import com.quiz.accesscontrol.entity.Role;
import com.quiz.accesscontrol.entity.Session;
import com.quiz.accesscontrol.entity.User;
import com.quiz.accesscontrol.exception.DuplicateUserException;
import com.quiz.accesscontrol.exception.InvalidUserDataException;
import com.quiz.accesscontrol.repository.MagicLinkRepository;
import com.quiz.accesscontrol.repository.RoleRepository;
import com.quiz.accesscontrol.repository.SessionRepository;
import com.quiz.accesscontrol.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quiz.accesscontrol.config.JwtUtil;
import com.quiz.accesscontrol.constants.Constant;

import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    @Autowired 
    private UserRepository userRepo;
    @Autowired 
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired 
    private SessionRepository sessionRepo;
    @Autowired 
    private MagicLinkRepository magicLinkRepo;
    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaProducerService kafkaProducer;

    

    public Map<String, String> createSuperAdmin(UserCreateRequestDTO request, String createdBy) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateUserException("A user with this email already exists");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidUserDataException("Password is required for SUPERADMIN");
        }

        Role superAdminRole = roleRepo.findByRoleName(Constant.SUPER_ADMIN)
                .orElseThrow(() -> new InvalidUserDataException("SUPERADMIN role not found"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(superAdminRole);
        user.setStatus(User.Status.ACTIVE);
        user.setInviteStatus(User.InviteStatus.ACTIVATED);
        user.setIsInvited(false);
        user.setCreatedBy(createdBy);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);

        return Map.of(
            "message", "User created successfully",
            "role", "SUPERADMIN",
            "createdBy", createdBy
        );
    }
    
    
 
    public String login(String email, String password, String deviceInfo, String ipAddress) {
        User user = userRepo.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User inactive or pending");
        }

        String token = jwtUtil.generateToken(user);

        Session session = new Session();
        session.setUser(user);
        session.setJwtToken(token);
        session.setIssuedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(1));
        session.setIsActive(true);
        session.setDeviceInfo(deviceInfo);
        session.setIpAddress(ipAddress);
        sessionRepo.save(session);

        return token;
    }


    public String inviteAdmin(String email, String fullName, String createdByEmail) throws JsonProcessingException {
        Role adminRole = roleRepo.findByRoleName(Constant.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));

        if (userRepo.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("User with this email already exists");
        }

        User creator = getActiveUserOrThrow(createdByEmail);

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(adminRole);
        user.setStatus(User.Status.INVITED);
        user.setInviteStatus(User.InviteStatus.PENDING);
        user.setIsInvited(true);
        user.setInvitedBy(creator);
        user.setCreatedBy(creator.getEmail());
        user.setCreatedAt(LocalDateTime.now());
      
        userRepo.save(user);
        
        String token = UUID.randomUUID().toString();

        MagicLink magicLinkEntity = new MagicLink();
        magicLinkEntity.setUser(user);
        magicLinkEntity.setTokenHash(token);
        magicLinkEntity.setPurpose(MagicLink.Purpose.INVITE);
        magicLinkEntity.setExpiresAt(LocalDateTime.now().plusHours(24));
        magicLinkEntity.setUsed(false);
        magicLinkEntity.setCreatedAt(LocalDateTime.now());
        magicLinkRepo.save(magicLinkEntity);

        // Send Kafka email event
        EmailEvent event = new EmailEvent();
        event.setEmail(email);
        event.setFullName(fullName);
        event.setMagicToken(token);
        event.setType("INVITE");
        event.setExpiresAt(magicLinkEntity.getExpiresAt());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(event);

        kafkaProducer.sendEmailEvent(payload);


        return token; 

    }


    public AcceptInviteResponseDTO acceptInvite(String email, String rawPassword) {
    	User user = getActiveUserOrThrow(email);

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setStatus(User.Status.ACTIVE);
        user.setInviteStatus(User.InviteStatus.ACTIVATED);
        user.setIsInvited(false);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepo.save(user);

        String token = jwtUtil.generateToken(savedUser);

        return new AcceptInviteResponseDTO(
                "Account activated successfully",
                savedUser.getStatus().name(),
                savedUser.getEmail(),
                savedUser.getRole().getRoleName(),
                token
        );
    }

    private User getActiveUserOrThrow(String email) {
        return userRepo.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }


 
    public void resetAdminPassword(String token, String newPassword) {
        MagicLink magicLink = magicLinkRepo.findByTokenHashAndUsedFalse(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid magic link"));

        if (magicLink.getExpiresAt().isBefore(LocalDateTime.now()) || magicLink.getUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Magic link expired or already used");
        }

        User admin = magicLink.getUser();
        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(admin);

        magicLink.setUsed(true);
        magicLinkRepo.save(magicLink);
       
    }
    

    public MagicLink generateAdminForgotPasswordLink(String adminEmail, String senderEmail, String ipAddress) {
        User admin = userRepo.findActiveByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        String token = UUID.randomUUID().toString();
        MagicLink magicLink = new MagicLink();
        magicLink.setUser(admin);
        magicLink.setTokenHash(token);
        magicLink.setPurpose(MagicLink.Purpose.RESET_PASSWORD);
        magicLink.setExpiresAt(LocalDateTime.now().plusHours(24));
        magicLink.setUsed(false);
        magicLink.setCreatedAt(LocalDateTime.now());
        magicLink.setDeliveryStatus(MagicLink.DeliveryStatus.SENT);
        magicLink.setIpAddress(ipAddress);

        magicLinkRepo.save(magicLink);
        return magicLink;
    }

   
  
    public void softDeleteAdmin(Long id, String deletedByEmail) {
        User user = userRepo.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        User deletedByUser = userRepo.findActiveByEmail(deletedByEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deleting user not found"));

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(deletedByUser);
        user.setStatus(User.Status.INACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public void restoreAdmin(Long id, String restoredByEmail) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        User restoredBy = userRepo.findActiveByEmail(restoredByEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restoring user not found"));

        user.setDeletedAt(null);
        user.setStatus(User.Status.ACTIVE);
        user.setUpdatedBy(restoredBy.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public void checkSuperAdminAccess(String email) {

    	User user = getActiveUserOrThrow(email);

        if (!user.getRole().getRoleName().equalsIgnoreCase("SUPERADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. SUPERADMIN only.");
        }
    }

   

    

}
