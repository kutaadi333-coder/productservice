package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.AuthResponse;
import com.ecommerce.productservice.dto.LoginRequest;
import com.ecommerce.productservice.dto.RefreshTokenRequest;
import com.ecommerce.productservice.dto.RegisterRequest;
import com.ecommerce.productservice.entity.User;
import com.ecommerce.productservice.repository.UserRepository;
import com.ecommerce.productservice.service.AuthService;
import com.ecommerce.productservice.service.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            UserRepository userRepository) {

        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // REGISTER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        try {

            User user =
                    authService.registerUser(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            "User registered successfully with email: "
                                    + user.getEmail()
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {

            User user =
                    authService.loginUser(request);

            String accessToken =
                    jwtService.generateAccessToken(user);

            String refreshToken =
                    jwtService.generateRefreshToken(user);

            AuthResponse response =
                    new AuthResponse(
                            accessToken,
                            refreshToken,
                            "Bearer",
                            jwtService.getAccessExpiration() / 1000
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // REFRESH TOKEN
    // ==========================================

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {

            String refreshToken =
                    request.getRefreshToken();

            // Validate refresh token
            if (!jwtService.validateToken(refreshToken)) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired refresh token");
            }

            // Check token type
            String tokenType =
                    jwtService.extractTokenType(refreshToken);

            if (!"REFRESH".equals(tokenType)) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid refresh token");
            }

            // Get user ID from token
            Long userId =
                    jwtService.extractUserId(refreshToken);

            // Find user in database
            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "User not found"
                                    )
                            );

            // Check user status
            if (!"ACTIVE".equalsIgnoreCase(
                    user.getStatus())) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User account is not active");
            }

            // Generate new access token
            String newAccessToken =
                    jwtService.generateAccessToken(user);

            AuthResponse response =
                    new AuthResponse(
                            newAccessToken,
                            refreshToken,
                            "Bearer",
                            jwtService.getAccessExpiration() / 1000
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token");
        }
    }
}