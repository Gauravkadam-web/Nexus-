package com.nexus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT Configuration properties bound from application.yml / env.
 */
@Configuration
@ConfigurationProperties(prefix = "nexus.jwt")
public class JwtConfig {

    /**
     * Secret key for signing JWT tokens (min 256 bits / 32 chars).
     */
    private String secret = "default-dev-secret-key-that-is-at-least-32-bytes-long-change-in-prod";

    /**
     * Access token validity in milliseconds (default: 15 minutes = 900,000 ms).
     */
    private long accessTokenExpirationMs = 900000;

    /**
     * Refresh token validity in milliseconds (default: 7 days = 604,800,000 ms).
     */
    private long refreshTokenExpirationMs = 604800000;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) {
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }
}
