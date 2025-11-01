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
                15,
                now,
                now,
                0,
                2,
                now,
                now
        );

        String json = mapper.writeValueAsString(res);

        assertThat(json).contains("\"isActivity\":true");

        TaskRes roundTrip = mapper.readValue(json, TaskRes.class);
        assertThat(roundTrip.activity()).isTrue();
        assertThat(roundTrip.startDay()).isEqualTo(0);
        assertThat(roundTrip.endDay()).isEqualTo(2);
    }

    @Test
    void taskCreateReqDeserializesIsActivity() throws IOException {
        String json = """
                {
                  "projectId": "00000000-0000-0000-0000-000000000010",
                  "title": "Example",
                  "description": "Details",
                  "isActivity": true,
                  "duration": 45,
                  "startAt": "2023-12-31T00:00:00Z",
                  "endAt": "2024-01-01T00:00:00Z"
                }
                """;

        TaskCreateReq req = mapper.readValue(json, TaskCreateReq.class);

        assertThat(req.activity()).isTrue();
        assertThat(req.duration()).isEqualTo(45);
        assertThat(req.startAt()).isEqualTo(Instant.parse("2023-12-31T00:00:00Z"));
    }

    @Test
    void taskUpdateReqRespectsIsActivity() throws IOException {
        String json = """
                {
                  "title": "Example",
                  "isActivity": false,
                  "duration": 10,
                  "startAt": "2024-01-05T00:00:00Z"
                }
                """;

        TaskUpdateReq req = mapper.readValue(json, TaskUpdateReq.class);

        assertThat(req.activity()).isFalse();
        assertThat(req.duration()).isEqualTo(10);
        assertThat(req.startAt()).isEqualTo(Instant.parse("2024-01-05T00:00:00Z"));
    }
}
