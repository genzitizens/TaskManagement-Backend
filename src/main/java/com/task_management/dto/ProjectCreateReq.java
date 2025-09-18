package com.task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateReq(
        @NotBlank @Size(max=160) String name,
        @Size(max=10_000) String description
) {}
