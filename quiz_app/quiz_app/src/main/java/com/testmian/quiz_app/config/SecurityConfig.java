package com.testmian.quiz_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                         // Allow public (candidate) endpoints
                         .requestMatchers(
                                 "/api/quiz-attempt/start",
                                 "/api/quiz-attempt/**",
                                 "/api/scores/**",
                                 "/api/analytics/**",
                                 "/api/magic-link/**",
                                 "/api/quiz-monitoring/**"
                         ).permitAll()

                        // Everything else still requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}