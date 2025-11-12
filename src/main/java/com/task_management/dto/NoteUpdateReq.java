package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NoteUpdateReq(
        @Size(max = 20_000) String body
) {
}