package com.task_management.dto;

import java.time.Instant;
import java.util.UUID;

public record ActionRes(
        UUID id,
        UUID taskId,
        String details,
        Integer day,
        Instant createdAt,
        Instant updatedAt
) {
}