package com.task_management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task_management.dto.NoteCreateReq;
import com.task_management.dto.NoteRes;
import com.task_management.service.NoteService;
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

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @Test
    void create_returnsCreatedNote() throws Exception {
        UUID projectId = UUID.randomUUID();
        NoteCreateReq request = new NoteCreateReq(projectId, null, "Body");
        NoteRes response = new NoteRes(
                UUID.randomUUID(),
                projectId,
                null,
                "Body",
                Instant.parse("2024-01-01T00:00:00Z")
        );
        when(noteService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()));

        ArgumentCaptor<NoteCreateReq> captor = ArgumentCaptor.forClass(NoteCreateReq.class);
        verify(noteService).create(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void list_forProjectReturnsNotes() throws Exception {
        UUID projectId = UUID.randomUUID();
        NoteRes response = new NoteRes(
                UUID.randomUUID(),
                projectId,
                null,
                "Body",
                Instant.parse("2024-01-01T00:00:00Z")
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<NoteRes> page = new PageImpl<>(List.of(response), pageable, 1);
        when(noteService.listForProject(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/notes").param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectId").value(projectId.toString()));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(noteService).listForProject(idCaptor.capture(), pageableCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(projectId);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void list_forTaskReturnsNotes() throws Exception {
        UUID taskId = UUID.randomUUID();
        NoteRes response = new NoteRes(
                UUID.randomUUID(),
                null,
                taskId,
                "Body",
                Instant.parse("2024-01-01T00:00:00Z")
        );
        Pageable pageable = PageRequest.of(0, 20);
        Page<NoteRes> page = new PageImpl<>(List.of(response), pageable, 1);
        when(noteService.listForTask(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/notes").param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].taskId").value(taskId.toString()));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(noteService).listForTask(idCaptor.capture(), pageableCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(taskId);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void list_whenBothIdentifiersProvided_returnsBadRequest() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/notes")
                        .param("projectId", projectId.toString())
                        .param("taskId", taskId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Provide either projectId or taskId"));

        verify(noteService, never()).listForProject(any(), any());
        verify(noteService, never()).listForTask(any(), any());
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        UUID noteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/notes/{id}", noteId))
                .andExpect(status().isNoContent());

        verify(noteService).delete(noteId);
    }
}
