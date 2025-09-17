package com.task_management.dto;

import java.time.Instant;
import java.util.UUID;

public record NoteRes(
        UUID id,
        UUID projectId,
        UUID taskId,
        String body,
        Instant createdAt
) {}

