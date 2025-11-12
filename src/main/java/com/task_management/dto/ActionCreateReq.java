package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ActionCreateReq(
        @NotNull UUID taskId,
        @NotBlank @Size(max = 10_000) String details,
        @NotNull Integer day
) {
}