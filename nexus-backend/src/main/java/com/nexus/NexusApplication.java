package com.nexus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Nexus - AI-Powered Case Management Platform
 * Main Spring Boot Application Entry Point.
 */
@SpringBootApplication
@EnableScheduling
public class NexusApplication {

    private static final Logger log = LoggerFactory.getLogger(NexusApplication.class);

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(NexusApplication.class, args);
    }

    /**
     * Automatically loads .env file into System properties if present.
     * Allows local development secrets to remain outside command line arguments.
     */
    private static void loadDotEnv() {
        File[] searchLocations = new File[]{
                new File(".env"),
                new File("nexus-backend/.env"),
                new File("../.env")
        };

        for (File envFile : searchLocations) {
            if (envFile.exists() && envFile.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();

                        // Strip optional surrounding quotes
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }

                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                    log.info("[Config] Successfully loaded environment variables from: {}", envFile.getAbsolutePath());
                    return;
                } catch (IOException e) {
                    log.warn("[Config] Could not read .env file from {}: {}", envFile.getAbsolutePath(), e.getMessage());
                }
            }
        }
    }
}
