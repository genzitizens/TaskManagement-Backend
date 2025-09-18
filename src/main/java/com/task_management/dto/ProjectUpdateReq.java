package com.task_management.dto;

import jakarta.validation.constraints.Size;


public record ProjectUpdateReq(
        @Size(max=160) String name,
        @Size(max=10_000) String description
) {}

