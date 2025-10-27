package com.example.quiz.config;

import com.example.quiz.entity.MagicLink;
import com.example.quiz.repository.MagicLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MagicLinkAuthenticationFilter extends OncePerRequestFilter {

    private final MagicLinkRepository magicLinkRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tokenHash = request.getHeader("Magic-Token");

        if (tokenHash != null && !tokenHash.isEmpty()) {
            Optional<MagicLink> optionalLink = magicLinkRepository.findByTokenHash(tokenHash);

            if (optionalLink.isPresent()) {
                MagicLink magicLink = optionalLink.get();

                // Check if link is not used and not expired
                if (!Boolean.TRUE.equals(magicLink.getUsed()) && magicLink.getExpiresAt().isAfter(LocalDateTime.now())) {
                    String roleName = magicLink.getUser().getRole().getRoleName();
                    String role = "ROLE_" + (roleName != null ? roleName.toUpperCase() : "USER");

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    magicLink.getUser().getEmail(),
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // Skip filter for public endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }
}
