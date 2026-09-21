package com.ecommerce.productservice.config;

import com.ecommerce.productservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .headers(headers ->
                        headers.frameOptions(frameOptions -> frameOptions.disable())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/v1/products/public",
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // USER + MANAGER + ADMIN
                        // View products
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/v1/products",
                                "/api/v1/products/**"
                        ).hasAnyRole("USER", "MANAGER", "ADMIN")

                        // MANAGER + ADMIN
                        // Create product
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/products"
                        ).hasAnyRole("MANAGER", "ADMIN")

                        // MANAGER + ADMIN
                        // Update product
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/products/**"
                        ).hasAnyRole("MANAGER", "ADMIN")

                        // MANAGER + ADMIN
                        // Partial update product
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PATCH,
                                "/api/v1/products/**"
                        ).hasAnyRole("MANAGER", "ADMIN")

                        // ADMIN only
                        // Delete product
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/v1/products/**"
                        ).hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .httpBasic(httpBasic -> {});

        return http.build();
    }
}