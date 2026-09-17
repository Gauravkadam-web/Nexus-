package com.nexus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger configuration for Nexus Platform Backend.
 * Configures global JWT Bearer Security Scheme for interactive API exploration.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nexus — AI-Powered Case Management Platform API")
                        .version("1.0.0")
                        .description("REST API Documentation covering all 7 Delivery Phases (US-1 to US-35):\n" +
                                "- Core Case Management & Lifecycle State Machine\n" +
                                "- Communication, Notes & Evidence Attachments\n" +
                                "- AI Case Intelligence (Classification, Summarization, HITL Suggestions)\n" +
                                "- Related Cases, Duplicate Detection & Smart Operator Routing\n" +
                                "- SLA Tracking, Automated Risk Scoring & Escalation Triggers\n" +
                                "- Resolution Management, Problem Root-Cause & AI Copilot\n" +
                                "- Manager Analytics, Immutable Audit Logs & Security Rate Limiting")
                        .contact(new Contact()
                                .name("Nexus Engineering")
                                .email("support@nexus.io"))
                        .license(new License().name("Proprietary").url("https://nexus.io/license")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT Access Token (without 'Bearer ' prefix).")));
    }
}
