package com.nexus.health.controller;

import com.nexus.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint for monitoring and frontend connectivity verification.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", "Nexus Backend");
        healthData.put("version", "1.0.0");
        healthData.put("timestamp", Instant.now().toString());

        return ResponseEntity.ok(ApiResponse.success(healthData, "Nexus Backend is healthy and operational"));
    }
}
