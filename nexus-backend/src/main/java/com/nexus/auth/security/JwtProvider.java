package com.nexus.auth.security;

import com.nexus.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final JwtConfig jwtConfig;
    private final SecretKey key;

    public JwtProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Ensure 256 bits minimum key length
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpirationMs());

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .claim("email", userPrincipal.getUsername())
                .claim("name", userPrincipal.getName())
                .claim("orgId", userPrincipal.getOrganizationId() != null ? userPrincipal.getOrganizationId().toString() : null)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpirationMs());

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .claim("email", userPrincipal.getUsername())
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.error("Invalid JWT signature/token format: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
