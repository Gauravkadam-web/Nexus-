package com.nexus.auth.security;

import com.nexus.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("very-secret-test-jwt-key-that-is-at-least-32-chars-long");
        jwtConfig.setAccessTokenExpirationMs(3600000);
        jwtConfig.setRefreshTokenExpirationMs(7200000);

        jwtProvider = new JwtProvider(jwtConfig);

        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        testPrincipal = new UserPrincipal(
                userId,
                orgId,
                "Test User",
                "test@nexus.com",
                "password",
                true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_REQUESTER"))
        );
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        String token = jwtProvider.generateAccessToken(testPrincipal);
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(testPrincipal.getId(), jwtProvider.getUserIdFromToken(token));
        assertEquals("test@nexus.com", jwtProvider.getEmailFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        String token = jwtProvider.generateRefreshToken(testPrincipal);
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(testPrincipal.getId(), jwtProvider.getUserIdFromToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtProvider.validateToken("invalid.jwt.token"));
    }
}
