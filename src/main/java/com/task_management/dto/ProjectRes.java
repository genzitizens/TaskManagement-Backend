package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;


public record ProjectRes(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}