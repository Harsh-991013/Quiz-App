package com.example.quiz.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long defaultExpirationMillis;

    public String generateToken(String subject, String role) {
        return generateToken(subject, role, defaultExpirationMillis);
    }

    public String generateToken(String subject, String role, long ttlMillis) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
