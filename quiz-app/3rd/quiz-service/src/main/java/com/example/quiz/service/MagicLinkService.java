package com.example.quiz.service;

import com.example.quiz.entity.MagicLink;
import com.example.quiz.entity.User;
import com.example.quiz.repository.MagicLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MagicLinkService {

    private final MagicLinkRepository magicLinkRepository;
    private final EmailService emailService;

    @Value("${app.magic-link.expiration-hours}")
    private int expirationHours;

    @Transactional
    public MagicLink generateMagicLink(User user, MagicLink.Purpose purpose) {
        String token = UUID.randomUUID().toString();
        String tokenHash = hashToken(token);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(expirationHours);

        MagicLink magicLink = new MagicLink();
        magicLink.setUser(user);
        magicLink.setTokenHash(tokenHash);
        magicLink.setPurpose(purpose);
        magicLink.setExpiresAt(expiresAt);
        magicLink.setUsed(false);

        MagicLink saved = magicLinkRepository.save(magicLink);

        String magicLinkUrl = "http://localhost:8080/auth/magic-link?token=" + token;
        emailService.sendMagicLinkEmail(
                user.getEmail(),
                user.getFullName() != null ? user.getFullName() : user.getEmail(),
                magicLinkUrl,
                purpose,
                expiresAt
        );

        return saved;
    }

    public Optional<MagicLink> validateMagicLink(String token) {
        String tokenHash = hashToken(token);

        return magicLinkRepository.findByTokenHash(tokenHash)
                .filter(link -> !Boolean.TRUE.equals(link.getUsed()))
                .filter(link -> link.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public boolean useMagicLink(String token) {
        Optional<MagicLink> magicLinkOpt = validateMagicLink(token);
        if (magicLinkOpt.isPresent()) {
            MagicLink magicLink = magicLinkOpt.get();
            magicLink.setUsed(true);
            magicLink.setUsedAt(LocalDateTime.now());
            magicLinkRepository.save(magicLink);
            return true;
        }
        return false;
    }

    public User getUserFromToken(String token) {
        String tokenHash = hashToken(token);
        return magicLinkRepository.findByTokenHash(tokenHash)
                .map(MagicLink::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
