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
import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import com.task_management.service.TaskService;
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

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void create_returnsCreatedTask() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskCreateReq request = new TaskCreateReq(projectId, "Title", "Desc", true,
                90,
                Instant.parse("2024-02-01T00:00:00Z"));
        TaskRes response = new TaskRes(
                UUID.randomUUID(),
                projectId,
                "Title",
                "Desc",
                true,
                90,
                request.endAt(),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(taskService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.isActivity").value(true))
                .andExpect(jsonPath("$.duration").value(90));

        ArgumentCaptor<TaskCreateReq> captor = ArgumentCaptor.forClass(TaskCreateReq.class);
        verify(taskService).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void get_returnsTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskRes response = new TaskRes(
                taskId,
                UUID.randomUUID(),
                "Title",
                "Desc",
                false,
                45,
                Instant.parse("2024-02-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(taskService.get(taskId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.isActivity").value(false))
                .andExpect(jsonPath("$.duration").value(45));

        verify(taskService).get(taskId);
    }

    @Test
    void listByProject_returnsTasks() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRes response = new TaskRes(
                UUID.randomUUID(),
                projectId,
                "Title",
                "Desc",
                false,
                30,
                Instant.parse("2024-02-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<TaskRes> page = new PageImpl<>(List.of(response), pageable, 1);
        when(taskService.listInProject(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/tasks").param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.content[0].isActivity").value(false))
                .andExpect(jsonPath("$.content[0].duration").value(30));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskService).listInProject(idCaptor.capture(), pageableCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(projectId);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void update_returnsUpdatedTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateReq request = new TaskUpdateReq("Updated", "New", false,
                120,
                Instant.parse("2024-03-01T00:00:00Z"));
        TaskRes response = new TaskRes(
                taskId,
                UUID.randomUUID(),
                "Updated",
                "New",
                false,
                120,
                request.endAt(),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(taskService.update(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.isActivity").value(false))
                .andExpect(jsonPath("$.duration").value(120));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<TaskUpdateReq> reqCaptor = ArgumentCaptor.forClass(TaskUpdateReq.class);
        verify(taskService).update(idCaptor.capture(), reqCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(taskId);
        assertThat(reqCaptor.getValue()).isEqualTo(request);
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).delete(taskId);
    }
}
