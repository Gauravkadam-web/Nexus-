package com.nexus.ai.provider;

/**
 * Thrown when the AI provider is unavailable or returns an invalid response.
 * Callers must catch this and handle graceful degradation — never let it block
 * core case management operations (US-15).
 */
public class AiUnavailableException extends RuntimeException {

    public AiUnavailableException(String message) {
        super(message);
    }

    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
