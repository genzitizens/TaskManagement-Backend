package com.task_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Management API",
                version = "v1",
                description = "REST API for managing projects, tasks and notes.",
                contact = @Contact(name = "Task Management Team", email = "support@example.com")
        ),
        servers = {
                @Server(url = "http://localhost:8002", description = "Local server (default port)")
        },
        tags = {
                @Tag(name = "Projects", description = "Operations about projects"),
                @Tag(name = "Tasks", description = "Operations about tasks"),
                @Tag(name = "Notes", description = "Operations about notes")
        }
)
public class OpenApiConfig {
}
