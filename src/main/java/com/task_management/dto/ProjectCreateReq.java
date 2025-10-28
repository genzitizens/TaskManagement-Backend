package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectCreateReq(
        @NotBlank @Size(max=160) String name,
        @Size(max=10_000) String description,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate
) {}
