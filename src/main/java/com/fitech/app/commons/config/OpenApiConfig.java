package com.fitech.app.commons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitechOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FITECH API")
                        .description("Fitness platform API that connects trainers with clients, offering personalized training services and premium memberships.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("FITECH Development Team")
                                .email("dev@fitech.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://fitech.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development server"),
                        new Server()
                                .url("https://appfitech.com")
                                .description("Production server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("fitech-api")
                .pathsToMatch("/v1/app/**")
                .build();
    }
} 