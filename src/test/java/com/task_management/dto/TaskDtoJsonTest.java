package com.task_management.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TaskDtoJsonTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void taskResSerializesIsActivityProperty() throws IOException {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        TaskRes res = new TaskRes(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "Title",
                "Desc",
                true,
                now,
                now,
                now
        );

        String json = mapper.writeValueAsString(res);

        assertThat(json).contains("\"isActivity\":true");

        TaskRes roundTrip = mapper.readValue(json, TaskRes.class);
        assertThat(roundTrip.isActivity()).isTrue();
    }

    @Test
    void taskCreateReqDeserializesIsActivity() throws IOException {
        String json = """
                {
                  "projectId": "00000000-0000-0000-0000-000000000010",
                  "title": "Example",
                  "description": "Details",
                  "isActivity": true,
                  "endAt": "2024-01-01T00:00:00Z"
                }
                """;

        TaskCreateReq req = mapper.readValue(json, TaskCreateReq.class);

        assertThat(req.isActivity()).isTrue();
    }

    @Test
    void taskUpdateReqRespectsIsActivity() throws IOException {
        String json = """
                {
                  "title": "Example",
                  "isActivity": false
                }
                """;

        TaskUpdateReq req = mapper.readValue(json, TaskUpdateReq.class);

        assertThat(req.isActivity()).isFalse();
    }
}
