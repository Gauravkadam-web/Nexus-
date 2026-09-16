package com.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Nexus - AI-Powered Case Management Platform
 * Main Spring Boot Application Entry Point.
 */
@SpringBootApplication
@EnableScheduling
public class NexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
    }
}
