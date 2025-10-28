package com.testmian.quiz_app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.testmian.quiz_app.config.JwtUtil;
import com.testmian.quiz_app.constants.Constant;
import com.testmian.quiz_app.dto.AcceptInviteResponseDTO;
import com.testmian.quiz_app.dto.EmailEvent;
import com.testmian.quiz_app.dto.UserCreateRequestDTO;
import com.testmian.quiz_app.entity.AuthMethod;
import com.testmian.quiz_app.entity.DeliveryStatus;
import com.testmian.quiz_app.entity.InviteStatus;
import com.testmian.quiz_app.entity.MagicLink;
import com.testmian.quiz_app.entity.MagicLinkPurpose;
import com.testmian.quiz_app.entity.Role;
import com.testmian.quiz_app.entity.Session;
import com.testmian.quiz_app.entity.User;
import com.testmian.quiz_app.entity.UserStatus;
import com.testmian.quiz_app.exception.DuplicateUserException;
import com.testmian.quiz_app.exception.InvalidUserDataException;
import com.testmian.quiz_app.repository.AuthMethodRepository;
import com.testmian.quiz_app.repository.DeliveryStatusRepository;
import com.testmian.quiz_app.repository.InviteStatusRepository;
import com.testmian.quiz_app.repository.MagicLinkPurposeRepository;
import com.testmian.quiz_app.repository.MagicLinkRepository;
import com.testmian.quiz_app.repository.RoleRepository;
import com.testmian.quiz_app.repository.SessionRepository;
import com.testmian.quiz_app.repository.UserRepository;
import com.testmian.quiz_app.repository.UserStatusRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;

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
    private InviteStatusRepository inviteStatusRepo;
    @Autowired
    private AuthMethodRepository authMethodRepo;

    @Autowired
    private UserStatusRepository userStatusRepo;

    @Autowired
    private MagicLinkPurposeRepository magicLinkPurposeRepo;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaProducerService kafkaProducer;

    

    public Map<String, String> createSuperAdmin(UserCreateRequestDTO request, String createdByEmail) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateUserException("A user with this email already exists");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidUserDataException("Password is required for SUPERADMIN");
        }

        Role superAdminRole = roleRepo.findByRoleName("SUPERADMIN")
                .orElseThrow(() -> new InvalidUserDataException("SUPERADMIN role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(superAdminRole);

        InviteStatus pendingStatus = inviteStatusRepo.findByStatusKey("PENDING")
                .orElseThrow(() -> new RuntimeException("Invite status PENDING not found"));
        user.setInviteStatus(pendingStatus);

        // ✅ Set createdBy as numeric ID
        if (createdByEmail != null && !createdByEmail.equalsIgnoreCase("system")) {
            userRepo.findByEmail(createdByEmail)
                    .ifPresent(creator -> user.setCreatedBy(creator.getUserId()));
        } else {
            user.setCreatedBy(null); // first superadmin
        }

        user.setUpdatedBy(user.getCreatedBy());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        return Map.of(
            "message", "User created successfully",
            "role", "SUPERADMIN",
            "createdBy", createdByEmail != null ? createdByEmail : "SYSTEM"
        );
    }

    
    
 
    public String login(String email, String password, String deviceInfo, String ipAddress) {
        User user = userRepo.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
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

    
    public String validateAndGetSuperAdmin(String token) {
    	 jwtUtil.validateToken(token);
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        checkSuperAdminAccess(email);
        return email;
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

        // Set invite status to PENDING
        InviteStatus pendingStatus = inviteStatusRepo.findByStatusKey("PENDING")
                .orElseThrow(() -> new RuntimeException("Invite status PENDING not found"));
        user.setInviteStatus(pendingStatus);

        UserStatus activeStatus = userStatusRepo.findByStatusKey("ACTIVE")
                .orElseThrow(() -> new RuntimeException("User status ACTIVE not found"));
        user.setStatus(activeStatus);

        user.setInvitedBy(creator);
        user.setCreatedBy(creator.getUserId());
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        
     // inviteAdmin()
        AuthMethod magicLinkMethod = authMethodRepo.findByMethodKey("MAGIC_LINK")
                .orElseThrow(() -> new RuntimeException("Auth method MAGIC_LINK not found"));
        user.setAuthMethod(magicLinkMethod);
        userRepo.save(user);

        String token = UUID.randomUUID().toString();

        MagicLink magicLinkEntity = new MagicLink();
        magicLinkEntity.setUser(user);
        magicLinkEntity.setTokenHash(token);
        MagicLinkPurpose invitePurpose = magicLinkPurposeRepo.findByPurposeKey("INVITE")
                .orElseThrow(() -> new RuntimeException("Magic link purpose INVITE not found"));
        magicLinkEntity.setPurpose(invitePurpose);
        magicLinkEntity.setExpiresAt(LocalDateTime.now().plusHours(24));	
        magicLinkEntity.setUsed(false);
        magicLinkEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        DeliveryStatus sentStatus = deliveryStatusRepo.findByStatusKey("SENT")
                .orElseThrow(() -> new RuntimeException("Delivery status SENT not found"));
        magicLinkEntity.setDeliveryStatus(sentStatus);
        magicLinkRepo.save(magicLinkEntity);

        // Send Kafka email event
        EmailEvent event = new EmailEvent();
        event.setEmail(email);
        event.setFullName(fullName);
        event.setMagicToken(token);
        event.setType("INVITE");
        event.setExpiresAt(magicLinkEntity.getExpiresAt());

        //event send to kafka
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(event);
        kafkaProducer.sendEmailEvent(payload);

        return token; 
    }



    public AcceptInviteResponseDTO acceptInvite(String email, String rawPassword) {
        User user = getActiveUserOrThrow(email);

        // Update password
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        // Set invite status to ACTIVATED
        InviteStatus activatedStatus = inviteStatusRepo.findByStatusKey("ACTIVATED")
                .orElseThrow(() -> new RuntimeException("Invite status ACTIVATED not found"));
        user.setInviteStatus(activatedStatus);

        // Set user status to ACTIVE
        UserStatus activeStatus = userStatusRepo.findByStatusKey("ACTIVE")
                .orElseThrow(() -> new RuntimeException("User status ACTIVE not found"));
        user.setStatus(activeStatus);

        user.setUpdatedAt(LocalDateTime.now());
        
        AuthMethod passwordMethod = authMethodRepo.findByMethodKey("PASSWORD")
                .orElseThrow(() -> new RuntimeException("Auth method PASSWORD not found"));
        user.setAuthMethod(passwordMethod);

        User savedUser = userRepo.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser);

        return new AcceptInviteResponseDTO(
                "Account activated successfully",
                savedUser.getEmail(),
                savedUser.getRole().getRoleName(),
                token, token
        );
    }

    
    public MagicLink generateAdminForgotPasswordLink(String adminEmail, String senderEmail, String ipAddress) {
        User admin = userRepo.findActiveByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        String token = UUID.randomUUID().toString();

        MagicLink magicLink = new MagicLink();
        magicLink.setUser(admin);
        magicLink.setTokenHash(token);
        MagicLinkPurpose resetPasswordPurpose = magicLinkPurposeRepo.findByPurposeKey("RESET_PASSWORD")
        	    .orElseThrow(() -> new RuntimeException("Magic link purpose RESET_PASSWORD not found"));
        	magicLink.setPurpose(resetPasswordPurpose);
        magicLink.setExpiresAt(LocalDateTime.now().plusHours(24));
        magicLink.setUsed(false);
        magicLink.setCreatedAt(LocalDateTime.now());
        DeliveryStatus sentStatus = deliveryStatusRepo.findByStatusKey("SENT")
                .orElseThrow(() -> new RuntimeException("Delivery status SENT not found"));
        magicLink.setDeliveryStatus(sentStatus);

        magicLink.setIpAddress(ipAddress);

        magicLinkRepo.save(magicLink);

        // ✅ Produce Kafka message for email
        try {
            Map<String, Object> payload = Map.of(
                    "email", admin.getEmail(),
                    "fullName", admin.getFullName(),
                    "magicToken", token,
                    "type", "RESET_PASSWORD",
                    "expiresAt", magicLink.getExpiresAt()
            );
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("password-reset-topic", message);
            System.out.println("✅ Password reset email message sent to Kafka");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return magicLink;
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
    

  
    public void softDeleteAdmin(Long id, String deletedByEmail) {
        User user = userRepo.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        User deletedByUser = userRepo.findActiveByEmail(deletedByEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deleting user not found"));

        // Set deletion info
        user.setDeletedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        user.setDeletedBy(deletedByUser);
        user.setUpdatedBy(deletedByUser.getUserId());

        // ✅ Update status to INACTIVE
        UserStatus inactiveStatus = userStatusRepo.findByStatusKey("INACTIVE")
                .orElseThrow(() -> new RuntimeException("User status INACTIVE not found"));
        user.setStatus(inactiveStatus);

        userRepo.save(user);
    }


    public void restoreAdmin(Long id, String restoredByEmail) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        User restoredBy = userRepo.findActiveByEmail(restoredByEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restoring user not found"));

        // Clear deletion info
        user.setDeletedAt(null);
        user.setUpdatedBy(restoredBy.getUserId());
        user.setUpdatedAt(LocalDateTime.now());

        // ✅ Set status back to ACTIVE
        UserStatus activeStatus = userStatusRepo.findByStatusKey("ACTIVE")
                .orElseThrow(() -> new RuntimeException("User status ACTIVE not found"));
        user.setStatus(activeStatus);

        userRepo.save(user);
    }


    public void checkSuperAdminAccess(String email) {
    	User user = getActiveUserOrThrow(email);
        if (!user.getRole().getRoleName().equalsIgnoreCase("SUPERADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. SUPERADMIN only.");
        }
    }
    
    private User getActiveUserOrThrow(String email) {
        return userRepo.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    

}
