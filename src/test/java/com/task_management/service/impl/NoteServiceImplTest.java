package com.task_management.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.task_management.dto.NoteCreateReq;
import com.task_management.dto.NoteRes;
import com.task_management.entity.Note;
import com.task_management.entity.Project;
import com.task_management.entity.Task;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.NoteMapper;
import com.task_management.repository.NoteRepository;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Project project;
    private Task task;
    private Note note;
    private NoteRes noteRes;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Project");

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setTitle("Task");
        task.setEndAt(Instant.parse("2024-01-01T00:00:00Z"));

        note = new Note();
        note.setId(UUID.randomUUID());
        note.setProject(project);
        note.setBody("Body");

        noteRes = new NoteRes(
                note.getId(),
                project.getId(),
                null,
                note.getBody(),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }

    @Test
    void create_whenBothTargetsMissing_throwsBadRequest() {
        NoteCreateReq request = new NoteCreateReq(null, null, "Body");

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> noteService.create(request))
                .withMessage("Provide either projectId or taskId");
    }

    @Test
    void create_whenBothTargetsProvided_throwsBadRequest() {
        UUID id = UUID.randomUUID();
        NoteCreateReq request = new NoteCreateReq(id, id, "Body");

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> noteService.create(request))
                .withMessage("Provide either projectId or taskId");
    }

    @Test
    void create_forProject_whenProjectMissing_throwsNotFound() {
        UUID projectId = project.getId();
        NoteCreateReq request = new NoteCreateReq(projectId, null, "Body");
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> noteService.create(request))
                .withMessage("Project not found");
    }

    @Test
    void create_forTask_whenTaskMissing_throwsNotFound() {
        UUID taskId = task.getId();
        NoteCreateReq request = new NoteCreateReq(null, taskId, "Body");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> noteService.create(request))
                .withMessage("Task not found");
    }

    @Test
    void create_forProject_savesNote() {
        UUID projectId = project.getId();
        NoteCreateReq request = new NoteCreateReq(projectId, null, "Body");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(noteRepository.save(any(Note.class))).thenReturn(note);
        when(noteMapper.toRes(note)).thenReturn(noteRes);

        NoteRes result = noteService.create(request);

        assertThat(result).isSameAs(noteRes);
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());
        Note saved = captor.getValue();
        assertThat(saved.getProject()).isSameAs(project);
        assertThat(saved.getTask()).isNull();
        assertThat(saved.getBody()).isEqualTo("Body");
    }

    @Test
    void create_forTask_savesNote() {
        UUID taskId = task.getId();
        NoteCreateReq request = new NoteCreateReq(null, taskId, "Body");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(noteRepository.save(any(Note.class))).thenReturn(note);
        when(noteMapper.toRes(note)).thenReturn(noteRes);

        NoteRes result = noteService.create(request);

        assertThat(result).isSameAs(noteRes);
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());
        Note saved = captor.getValue();
        assertThat(saved.getTask()).isSameAs(task);
        assertThat(saved.getProject()).isNull();
        assertThat(saved.getBody()).isEqualTo("Body");
    }

    @Test
    void listForProject_whenProjectMissing_throwsNotFound() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> noteService.listForProject(projectId, pageable))
                .withMessage("Project not found");
    }

    @Test
    void listForProject_returnsMappedPage() {
        UUID projectId = project.getId();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Note> page = new PageImpl<>(List.of(note), pageable, 1);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(noteRepository.findByProjectIdOrderByCreatedAtDesc(projectId, pageable)).thenReturn(page);
        when(noteMapper.toRes(note)).thenReturn(noteRes);

        Page<NoteRes> result = noteService.listForProject(projectId, pageable);

        assertThat(result.getContent()).containsExactly(noteRes);
    }

    @Test
    void listForTask_whenTaskMissing_throwsNotFound() {
        UUID taskId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> noteService.listForTask(taskId, pageable))
                .withMessage("Task not found");
    }

    @Test
    void listForTask_returnsMappedPage() {
        UUID taskId = task.getId();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Note> page = new PageImpl<>(List.of(note), pageable, 1);
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(noteRepository.findByTaskIdOrderByCreatedAtDesc(taskId, pageable)).thenReturn(page);
        when(noteMapper.toRes(note)).thenReturn(noteRes);

        Page<NoteRes> result = noteService.listForTask(taskId, pageable);

        assertThat(result.getContent()).containsExactly(noteRes);
    }

    @Test
    void delete_whenNoteMissing_throwsNotFound() {
        UUID noteId = UUID.randomUUID();
        when(noteRepository.existsById(noteId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> noteService.delete(noteId))
                .withMessage("Note not found");

        verify(noteRepository, never()).deleteById(noteId);
    }

    @Test
    void delete_whenNoteExists_deletesEntity() {
        UUID noteId = UUID.randomUUID();
        when(noteRepository.existsById(noteId)).thenReturn(true);

        noteService.delete(noteId);

        verify(noteRepository).deleteById(noteId);
    }
}
