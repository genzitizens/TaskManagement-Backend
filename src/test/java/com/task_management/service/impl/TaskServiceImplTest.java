package com.task_management.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import com.task_management.entity.Project;
import com.task_management.entity.Task;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.TaskMapper;
import com.task_management.monitoring.TaskMetrics;
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
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskMetrics taskMetrics;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Project project;
    private Task task;
    private TaskRes taskRes;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Project");

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setTitle("Task");
        task.setDescription("Description");
        task.setActivity(true);
        task.setDuration(60);
        task.setEndAt(Instant.parse("2024-01-10T12:00:00Z"));

        taskRes = new TaskRes(
                task.getId(),
                project.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isActivity(),
                task.getDuration(),
                task.getEndAt(),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
    }

    @Test
    void create_whenProjectMissing_throwsNotFound() {
        UUID projectId = UUID.randomUUID();
        TaskCreateReq request = new TaskCreateReq(projectId, "Title", null, false, 30, Instant.now());
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> taskService.create(request))
                .withMessage("Project not found");
    }

    @Test
    void create_whenEndAtMissing_throwsBadRequest() {
        UUID projectId = project.getId();
        TaskCreateReq request = new TaskCreateReq(projectId, "Title", null, false, 30, null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> taskService.create(request))
                .withMessage("endAt is required");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_whenTitleBlankAfterTrim_throwsBadRequest() {
        UUID projectId = project.getId();
        TaskCreateReq request = new TaskCreateReq(projectId, "   ", null, false, 30, Instant.now());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> taskService.create(request))
                .withMessage("Task title required");
    }

    @Test
    void create_whenValid_savesTask() {
        UUID projectId = project.getId();
        Instant endAt = Instant.parse("2024-02-01T00:00:00Z");
        TaskCreateReq request = new TaskCreateReq(projectId, "  Important Task  ", "Desc", true, 30, endAt);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toRes(task)).thenReturn(taskRes);

        TaskRes result = taskService.create(request);

        assertThat(result).isSameAs(taskRes);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getProject()).isSameAs(project);
        assertThat(saved.getTitle()).isEqualTo("Important Task");
        assertThat(saved.getDescription()).isEqualTo("Desc");
        assertThat(saved.isActivity()).isTrue();
        assertThat(saved.getDuration()).isEqualTo(30);
        assertThat(saved.getEndAt()).isEqualTo(endAt);
        verify(taskMetrics).incrementCreated();
    }

    @Test
    void get_whenTaskExists_returnsResponse() {
        UUID taskId = task.getId();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toRes(task)).thenReturn(taskRes);

        TaskRes result = taskService.get(taskId);

        assertThat(result).isSameAs(taskRes);
    }

    @Test
    void get_whenTaskMissing_throwsNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> taskService.get(taskId))
                .withMessage("Task not found");
    }

    @Test
    void listInProject_whenProjectMissing_throwsNotFound() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> taskService.listInProject(projectId, pageable))
                .withMessage("Project not found");
    }

    @Test
    void listInProject_returnsMappedPage() {
        UUID projectId = project.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task), pageable, 1);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.findByProjectIdOrderByEndAtAsc(projectId, pageable)).thenReturn(page);
        when(taskMapper.toRes(task)).thenReturn(taskRes);

        Page<TaskRes> result = taskService.listInProject(projectId, pageable);

        assertThat(result.getContent()).containsExactly(taskRes);
    }

    @Test
    void update_whenTaskMissing_throwsNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> taskService.update(taskId, new TaskUpdateReq(null, null, null, null, null)))
                .withMessage("Task not found");
    }

    @Test
    void update_whenTitleBecomesBlank_throwsBadRequest() {
        UUID taskId = task.getId();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> taskService.update(taskId, new TaskUpdateReq("   ", null, null, null, null)))
                .withMessage("Task title cannot be blank");
    }

    @Test
    void update_whenEndAtMissingAfterUpdate_throwsBadRequest() {
        Task existing = new Task();
        existing.setId(UUID.randomUUID());
        existing.setProject(project);
        existing.setTitle("Existing");
        existing.setDescription("Desc");
        existing.setActivity(true);
        existing.setDuration(10);
        existing.setEndAt(null);
        UUID taskId = existing.getId();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> taskService.update(taskId, new TaskUpdateReq(null, null, null, null, null)))
                .withMessage("endAt is required");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void update_whenValid_updatesEntityAndReturnsResponse() {
        UUID taskId = task.getId();
        TaskUpdateReq request = new TaskUpdateReq("  Updated Title  ", "New Desc", false, 45, Instant.parse("2024-03-01T00:00:00Z"));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toRes(task)).thenReturn(taskRes);

        TaskRes result = taskService.update(taskId, request);

        assertThat(result).isSameAs(taskRes);
        verify(taskRepository).save(task);
        assertThat(task.getTitle()).isEqualTo("Updated Title");
        assertThat(task.getDescription()).isEqualTo("New Desc");
        assertThat(task.isActivity()).isFalse();
        assertThat(task.getDuration()).isEqualTo(45);
        assertThat(task.getEndAt()).isEqualTo(Instant.parse("2024-03-01T00:00:00Z"));
        verify(taskMetrics).incrementUpdated();
    }

    @Test
    void delete_whenTaskMissing_throwsNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> taskService.delete(taskId))
                .withMessage("Task not found");

        verify(taskRepository, never()).deleteById(taskId);
    }

    @Test
    void delete_whenTaskExists_deletesEntity() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.delete(taskId);

        verify(taskRepository).deleteById(taskId);
        verify(taskMetrics).incrementDeleted();
    }
}
