package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ActionUpdateReq(
        @Size(max = 10_000) String details,
        Integer day
) {
}