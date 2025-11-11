package com.task_management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task_management.dto.TagCreateReq;
import com.task_management.dto.TagRes;
import com.task_management.dto.TagUpdateReq;
import com.task_management.service.TagService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @Test
    void create_returnsCreatedTag() throws Exception {
        UUID projectId = UUID.randomUUID();
        TagCreateReq request = new TagCreateReq(projectId, "Title", "Desc", true,
                90,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-02-01T00:00:00Z"),
                "#AA11BB");
        TagRes response = new TagRes(
                UUID.randomUUID(),
                projectId,
                "Title",
                "Desc",
                true,
                90,
                request.startAt(),
                request.endAt(),
                0,
                31,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                "#AA11BB"
        );
        when(tagService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.isActivity").value(true))
                .andExpect(jsonPath("$.startAt").value(request.startAt().toString()))
                .andExpect(jsonPath("$.startDay").value(0))
                .andExpect(jsonPath("$.endDay").value(31))
                .andExpect(jsonPath("$.duration").value(90))
                .andExpect(jsonPath("$.color").value("#AA11BB"));

        ArgumentCaptor<TagCreateReq> captor = ArgumentCaptor.forClass(TagCreateReq.class);
        verify(tagService).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void get_returnsTag() throws Exception {
        UUID tagId = UUID.randomUUID();
        TagRes response = new TagRes(
                tagId,
                UUID.randomUUID(),
                "Title",
                "Desc",
                false,
                45,
                Instant.parse("2024-01-15T00:00:00Z"),
                Instant.parse("2024-02-01T00:00:00Z"),
                14,
                31,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                "#BB22CC"
        );
        when(tagService.get(tagId)).thenReturn(response);

        mockMvc.perform(get("/api/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagId.toString()))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.isActivity").value(false))
                .andExpect(jsonPath("$.startAt").value(response.startAt().toString()))
                .andExpect(jsonPath("$.startDay").value(14))
                .andExpect(jsonPath("$.endDay").value(31))
                .andExpect(jsonPath("$.duration").value(45))
                .andExpect(jsonPath("$.color").value("#BB22CC"));

        verify(tagService).get(tagId);
    }

    @Test
    void listByProject_returnsTags() throws Exception {
        UUID projectId = UUID.randomUUID();
        TagRes response = new TagRes(
                UUID.randomUUID(),
                projectId,
                "Title",
                "Desc",
                false,
                30,
                Instant.parse("2024-01-05T00:00:00Z"),
                Instant.parse("2024-02-01T00:00:00Z"),
                4,
                31,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                "#CC33DD"
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<TagRes> page = new PageImpl<>(List.of(response), pageable, 1);
        when(tagService.listInProject(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/tags").param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.content[0].isActivity").value(false))
                .andExpect(jsonPath("$.content[0].startAt").value(response.startAt().toString()))
                .andExpect(jsonPath("$.content[0].startDay").value(4))
                .andExpect(jsonPath("$.content[0].endDay").value(31))
                .andExpect(jsonPath("$.content[0].duration").value(30))
                .andExpect(jsonPath("$.content[0].color").value("#CC33DD"));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(tagService).listInProject(idCaptor.capture(), pageableCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(projectId);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void update_returnsUpdatedTag() throws Exception {
        UUID tagId = UUID.randomUUID();
        TagUpdateReq request = new TagUpdateReq("Updated", "New", false,
                120,
                Instant.parse("2024-02-01T00:00:00Z"),
                Instant.parse("2024-03-01T00:00:00Z"),
                "#DD44EE");
        TagRes response = new TagRes(
                tagId,
                UUID.randomUUID(),
                "Updated",
                "New",
                false,
                120,
                request.startAt(),
                request.endAt(),
                31,
                60,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                "#DD44EE"
        );
        when(tagService.update(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/tags/{id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagId.toString()))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.isActivity").value(false))
                .andExpect(jsonPath("$.startAt").value(request.startAt().toString()))
                .andExpect(jsonPath("$.startDay").value(31))
                .andExpect(jsonPath("$.endDay").value(60))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.color").value("#DD44EE"));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<TagUpdateReq> reqCaptor = ArgumentCaptor.forClass(TagUpdateReq.class);
        verify(tagService).update(idCaptor.capture(), reqCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(tagId);
        assertThat(reqCaptor.getValue()).isEqualTo(request);
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        UUID tagId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tags/{id}", tagId))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).delete(tagId);
    }
}
