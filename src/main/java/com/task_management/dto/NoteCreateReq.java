package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Create note targeting either a project or a task */
public record NoteCreateReq(
        UUID projectId,   // one of these must be set
        UUID taskId,
        @NotBlank @Size(max=20_000) String body
) {}
