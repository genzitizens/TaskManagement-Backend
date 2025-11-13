package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProjectImportReq(
        @NotNull UUID sourceProjectId,
        @NotBlank @Size(max = 160) String newProjectName,
        @Size(max = 10_000) String description,
        boolean importTasks,
        boolean importNotes,
        boolean importTags,
        boolean importActions
) {
}