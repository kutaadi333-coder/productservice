package com.ecommerce.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF for REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless REST API
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // API authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/v1/products/public",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // Protected Product APIs
                        .requestMatchers(
                                "/api/v1/products"
                        ).authenticated()

                        .requestMatchers(
                                "/api/v1/products/**"
                        ).authenticated()

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )

                // Basic authentication for Task 01
                .httpBasic(httpBasic -> {});

        return http.build();
    }
}