package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record TaskCreateReq(
        @NotNull UUID projectId,
        @NotBlank @Size(max = 160) String title,
        @Size(max = 10_000) String description,
        boolean isActivity,
        @NotNull Instant endAt
) {
}
