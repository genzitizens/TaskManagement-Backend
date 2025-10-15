package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record TaskRes(
        UUID id,
        UUID projectId,
        String title,
        String description,
        @JsonProperty("isActivity") boolean activity,
        Instant endAt,
        Instant createdAt,
        Instant updatedAt
) {
}
