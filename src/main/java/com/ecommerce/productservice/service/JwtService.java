package com.ecommerce.productservice.service;

import com.ecommerce.productservice.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

    private final String secret;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {

        this.secret = secret;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // ==========================================
    // GENERATE ACCESS TOKEN
    // ==========================================

    public String generateAccessToken(User user) {

        long issuedAt = Instant.now().getEpochSecond();

        long expiration =
                issuedAt + (accessExpiration / 1000);

        String header = createHeader();

        String payload = createPayload(
                user,
                issuedAt,
                expiration,
                "ACCESS"
        );

        return createToken(header, payload);
    }

    // ==========================================
    // GENERATE REFRESH TOKEN
    // ==========================================

    public String generateRefreshToken(User user) {

        long issuedAt = Instant.now().getEpochSecond();

        long expiration =
                issuedAt + (refreshExpiration / 1000);

        String header = createHeader();

        String payload = createPayload(
                user,
                issuedAt,
                expiration,
                "REFRESH"
        );

        return createToken(header, payload);
    }

    // ==========================================
    // CREATE JWT HEADER
    // ==========================================

    private String createHeader() {

        return "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    }

    // ==========================================
    // CREATE JWT PAYLOAD
    // ==========================================

    private String createPayload(
            User user,
            long issuedAt,
            long expiration,
            String tokenType) {

        return "{"
                + "\"userId\":" + user.getId() + ","
                + "\"email\":\"" + escapeJson(user.getEmail()) + "\","
                + "\"role\":\"" + escapeJson(user.getRole()) + "\","
                + "\"tokenType\":\"" + tokenType + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiration
                + "}";
    }

    // ==========================================
    // CREATE SIGNED JWT
    // ==========================================

    private String createToken(
            String header,
            String payload) {

        String encodedHeader =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                header.getBytes(StandardCharsets.UTF_8)
                        );

        String encodedPayload =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                payload.getBytes(StandardCharsets.UTF_8)
                        );

        String content =
                encodedHeader + "." + encodedPayload;

        String signature = sign(content);

        return content + "." + signature;
    }

    // ==========================================
    // SIGN JWT USING HMAC SHA-256
    // ==========================================

    private String sign(String content) {

        try {

            byte[] keyBytes =
                    Base64.getDecoder().decode(secret);

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            keyBytes,
                            "HmacSHA256"
                    );

            Mac mac =
                    Mac.getInstance("HmacSHA256");

            mac.init(secretKey);

            byte[] signature =
                    mac.doFinal(
                            content.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signature);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to create JWT signature",
                    e
            );
        }
    }

    // ==========================================
    // VALIDATE TOKEN SIGNATURE
    // ==========================================

    public boolean validateToken(String token) {

        try {

            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                return false;
            }

            String content =
                    parts[0] + "." + parts[1];

            String expectedSignature =
                    sign(content);

            if (!constantTimeEquals(
                    expectedSignature,
                    parts[2])) {

                return false;
            }

            long expiration =
                    extractExpiration(token);

            long currentTime =
                    Instant.now().getEpochSecond();

            return expiration > currentTime;

        } catch (Exception e) {

            return false;
        }
    }

    // ==========================================
    // EXTRACT USER ID
    // ==========================================

    public Long extractUserId(String token) {

        String payload = getPayload(token);

        String value =
                extractJsonValue(payload, "userId");

        return Long.parseLong(value);
    }

    // ==========================================
    // EXTRACT EMAIL
    // ==========================================

    public String extractEmail(String token) {

        String payload = getPayload(token);

        return extractJsonValue(payload, "email");
    }

    // ==========================================
    // EXTRACT ROLE
    // ==========================================

    public String extractRole(String token) {

        String payload = getPayload(token);

        return extractJsonValue(payload, "role");
    }

    // ==========================================
    // EXTRACT TOKEN TYPE
    // ==========================================

    public String extractTokenType(String token) {

        String payload = getPayload(token);

        return extractJsonValue(payload, "tokenType");
    }

    // ==========================================
    // EXTRACT EXPIRATION
    // ==========================================

    public long extractExpiration(String token) {

        String payload = getPayload(token);

        String value =
                extractJsonValue(payload, "exp");

        return Long.parseLong(value);
    }

    // ==========================================
    // GET JWT PAYLOAD
    // ==========================================

    private String getPayload(String token) {

        String[] parts =
                token.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid JWT token"
            );
        }

        byte[] decodedPayload =
                Base64.getUrlDecoder()
                        .decode(parts[1]);

        return new String(
                decodedPayload,
                StandardCharsets.UTF_8
        );
    }

    // ==========================================
    // EXTRACT JSON VALUE
    // ==========================================

    private String extractJsonValue(
            String json,
            String key) {

        String searchKey =
                "\"" + key + "\":";

        int start =
                json.indexOf(searchKey);

        if (start == -1) {
            throw new IllegalArgumentException(
                    "JWT claim not found: " + key
            );
        }

        start += searchKey.length();

        while (
                start < json.length()
                        && Character.isWhitespace(
                        json.charAt(start))
        ) {
            start++;
        }

        if (json.charAt(start) == '"') {

            start++;

            int end =
                    json.indexOf(
                            '"',
                            start
                    );

            if (end == -1) {
                throw new IllegalArgumentException(
                        "Invalid JWT payload"
                );
            }

            return json.substring(
                    start,
                    end
            );
        }

        int end =
                start;

        while (
                end < json.length()
                        && json.charAt(end) != ','
                        && json.charAt(end) != '}'
        ) {
            end++;
        }

        return json.substring(
                start,
                end
        ).trim();
    }

    // ==========================================
    // CONSTANT-TIME STRING COMPARISON
    // ==========================================

    private boolean constantTimeEquals(
            String first,
            String second) {

        byte[] firstBytes =
                first.getBytes(StandardCharsets.UTF_8);

        byte[] secondBytes =
                second.getBytes(StandardCharsets.UTF_8);

        return java.security.MessageDigest
                .isEqual(
                        firstBytes,
                        secondBytes
                );
    }

    // ==========================================
    // ESCAPE JSON
    // ==========================================

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // ==========================================
    // GET ACCESS EXPIRATION
    // ==========================================

    public long getAccessExpiration() {
        return accessExpiration;
    }
}