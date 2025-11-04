package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectRes(
        UUID id,
        String name,
        String description,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
        Instant createdAt,
        Instant updatedAt,
        List<TaskRes> tasks,
        List<TagRes> tags
) {}
