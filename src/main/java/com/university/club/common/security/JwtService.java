package com.university.club.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            SecretKey secretKey,
            @Value("${app.jwt.expiration}") long expiration) {

        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String generateToken(UserPrincipal user) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserPrincipal user) {

        try {
            String username = extractUsername(token);

            return username.equalsIgnoreCase(user.getUsername())
                    && extractAllClaims(token)
                    .getExpiration()
                    .after(new Date());

        } catch (io.jsonwebtoken.JwtException |
                 IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationSeconds() {
        return expiration / 1000;
    }
}