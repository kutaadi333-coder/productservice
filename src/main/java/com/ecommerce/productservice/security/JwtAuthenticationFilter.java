package com.ecommerce.productservice.security;

import com.ecommerce.productservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authorizationHeader =
                request.getHeader("Authorization");

        // If Authorization header is missing,
        // continue the request
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token =
                authorizationHeader.substring(7);

        try {

            // Validate JWT
            if (jwtService.validateToken(token)) {

                // Check token type
                String tokenType =
                        jwtService.extractTokenType(token);

                // Only ACCESS token can access
                // protected Product APIs
                if (!"ACCESS".equals(tokenType)) {

                    filterChain.doFilter(request, response);
                    return;
                }

                // Extract user information
                Long userId =
                        jwtService.extractUserId(token);

                String email =
                        jwtService.extractEmail(token);

                String role =
                        jwtService.extractRole(token);

                // Create authority from role
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority(
                                "ROLE_" + role
                        );

                // Create authenticated user
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(authority)
                        );

                // Store user ID as request attribute
                request.setAttribute(
                        "userId",
                        userId
                );

                // Set authentication in SecurityContext
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Invalid JWT
            SecurityContextHolder
                    .clearContext();
        }

        // Continue request
        filterChain.doFilter(
                request,
                response
        );
    }
}