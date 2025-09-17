package com.task_management.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TaskUpdateReq(
        @Size(max = 160) String title,
        String description,
        Boolean isActivity,
        Instant endAt
) {
}

