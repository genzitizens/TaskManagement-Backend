package com.task_management.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskRes(
        UUID id,
        UUID projectId,
        String title,
        String description,
        boolean isActivity,
        Instant endAt,
        Instant createdAt,
        Instant updatedAt
) {
}
