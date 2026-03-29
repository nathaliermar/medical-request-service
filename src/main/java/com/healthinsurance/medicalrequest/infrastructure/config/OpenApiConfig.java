package com.healthinsurance.medicalrequest.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicalRequestOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Medical Request Service API")
                        .description("Healthcare medical procedure request management for a health insurance company. " +
                                "Handles the full lifecycle of a medical authorisation request: " +
                                "creation, submission, review, approval/rejection.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Platform Engineering")
                                .email("platform@healthinsurance.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Provide a valid JWT token. Roles: BENEFICIARY, REVIEWER, ADMIN")));
    }
}
