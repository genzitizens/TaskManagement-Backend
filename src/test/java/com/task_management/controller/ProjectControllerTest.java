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
import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.service.ProjectService;
import java.time.Instant;
import java.time.LocalDate;
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

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @Test
    void create_returnsCreatedProject() throws Exception {
        ProjectCreateReq request = new ProjectCreateReq("Project", "Description", LocalDate.of(2024, 1, 15));
        ProjectRes response = new ProjectRes(
                UUID.randomUUID(),
                "Project",
                "Description",
                LocalDate.of(2024, 1, 15),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(projectService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.name").value("Project"))
                .andExpect(jsonPath("$.startDate").value("15-01-2024"));

        ArgumentCaptor<ProjectCreateReq> captor = ArgumentCaptor.forClass(ProjectCreateReq.class);
        verify(projectService).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void get_returnsProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectRes response = new ProjectRes(
                projectId,
                "Project",
                "Description",
                LocalDate.of(2024, 1, 15),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(projectService.get(projectId)).thenReturn(response);

        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Project"))
                .andExpect(jsonPath("$.startDate").value("15-01-2024"));

        verify(projectService).get(projectId);
    }

    @Test
    void list_returnsPageOfProjects() throws Exception {
        ProjectRes response = new ProjectRes(
                UUID.randomUUID(),
                "Project",
                "Description",
                LocalDate.of(2024, 1, 15),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProjectRes> page = new PageImpl<>(List.of(response), pageable, 1);
        when(projectService.list(any())).thenReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value("Project"))
                .andExpect(jsonPath("$.content[0].startDate").value("15-01-2024"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(projectService).list(captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void update_returnsUpdatedProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectUpdateReq request = new ProjectUpdateReq("Updated", "New description", LocalDate.of(2024, 1, 20));
        ProjectRes response = new ProjectRes(
                projectId,
                "Updated",
                "New description",
                LocalDate.of(2024, 1, 20),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
        when(projectService.update(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.startDate").value("20-01-2024"));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<ProjectUpdateReq> reqCaptor = ArgumentCaptor.forClass(ProjectUpdateReq.class);
        verify(projectService).update(idCaptor.capture(), reqCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(projectId);
        assertThat(reqCaptor.getValue()).isEqualTo(request);
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).delete(projectId);
    }
}
