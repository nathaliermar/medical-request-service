package com.healthinsurance.medicalrequest.infrastructure.config;

import com.healthinsurance.medicalrequest.infrastructure.security.JwtAuthenticationFilter;
import com.healthinsurance.medicalrequest.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            // Stateless — no HttpSession created or used
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/ci/healthcheck").permitAll()
                    // Swagger / OpenAPI — public
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    // Actuator health — public
                    .requestMatchers("/actuator/health").permitAll()
                    // Analysis submission — REVIEWER only
                    .requestMatchers(HttpMethod.POST, "/api/v1/requests/*/analysis").hasRole("REVIEWER")
                    // Pending items management — REVIEWER or ADMIN
                    .requestMatchers(HttpMethod.POST, "/api/v1/requests/*/pending-items").hasAnyRole("REVIEWER", "ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/requests/*/pending-items/*/resolve").hasAnyRole("REVIEWER", "ADMIN")
                    // Everything else requires authentication
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
