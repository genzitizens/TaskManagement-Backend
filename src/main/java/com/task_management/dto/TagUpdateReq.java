package com.task_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TagUpdateReq(
        @Size(max = 160) String title,
        String description,
        @JsonProperty("isActivity") Boolean activity,
        Integer duration,
        Instant startAt,
        Instant endAt
) {
}
