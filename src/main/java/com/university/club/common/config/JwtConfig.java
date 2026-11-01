package com.university.club.common.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    private final String secret;

    public JwtConfig(@Value("${app.jwt.secret}") String secret) {
        this.secret = secret;
    }

    @Bean
    public SecretKey jwtSecretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // JJWT enforces minimum lengths: HS256 ≥ 32 bytes, HS384 ≥ 48, HS512 ≥ 64.
        if (keyBytes.length < 64) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least 64 UTF-8 bytes (got "
                            + keyBytes.length + "). Set JWT_SECRET or edit application.yml.");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}