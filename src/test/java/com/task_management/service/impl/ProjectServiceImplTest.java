package com.task_management.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.entity.Project;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.ProjectMapper;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.TaskScheduleCalculator;
import java.time.Instant;
import java.time.LocalDate;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TaskScheduleCalculator scheduleCalculator;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRes projectRes;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Project Alpha");
        project.setDescription("Description");
        project.setStartDate(LocalDate.of(2024, 1, 10));
        project.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        project.setUpdatedAt(Instant.parse("2024-01-02T00:00:00Z"));

        projectRes = new ProjectRes(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    @Test
    void create_whenNameAlreadyExists_throwsBadRequest() {
        ProjectCreateReq request = new ProjectCreateReq("Project Alpha", "Description", LocalDate.of(2024, 1, 10));
        when(projectRepository.existsByNameIgnoreCase("Project Alpha")).thenReturn(true);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> projectService.create(request))
                .withMessage("Project name already exists");

        verify(projectRepository, never()).save(any());
    }

    @Test
    void create_whenValid_savesAndReturnsResponse() {
        ProjectCreateReq request = new ProjectCreateReq("Project Beta", "Description", LocalDate.of(2024, 1, 10));
        Project entityToSave = new Project();
        entityToSave.setStartDate(LocalDate.of(2024, 1, 10));
        when(projectRepository.existsByNameIgnoreCase("Project Beta")).thenReturn(false);
        when(projectMapper.toEntity(request)).thenReturn(entityToSave);
        when(projectRepository.save(entityToSave)).thenReturn(project);
        when(projectMapper.toRes(project)).thenReturn(projectRes);

        ProjectRes result = projectService.create(request);

        assertThat(result).isSameAs(projectRes);
        verify(projectRepository).save(entityToSave);
    }

    @Test
    void get_whenProjectExists_returnsResponse() {
        UUID id = project.getId();
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectMapper.toRes(project)).thenReturn(projectRes);

        ProjectRes result = projectService.get(id);

        assertThat(result).isSameAs(projectRes);
    }

    @Test
    void get_whenProjectMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> projectService.get(id))
                .withMessage("Project not found");
    }

    @Test
    void list_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> page = new PageImpl<>(List.of(project), pageable, 1);
        when(projectRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(page);
        when(projectMapper.toRes(project)).thenReturn(projectRes);

        Page<ProjectRes> result = projectService.list(pageable);

        assertThat(result.getContent()).containsExactly(projectRes);
    }

    @Test
    void update_whenProjectMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> projectService.update(id, new ProjectUpdateReq("Name", null, LocalDate.of(2024, 1, 10))))
                .withMessage("Project not found");
    }

    @Test
    void update_whenNewNameAlreadyExists_throwsBadRequest() {
        UUID id = project.getId();
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectRepository.existsByNameIgnoreCase("Another")).thenReturn(true);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> projectService.update(id, new ProjectUpdateReq("Another", null, null)))
                .withMessage("Project name already exists");

        verify(projectRepository, never()).save(any());
    }

    @Test
    void update_whenValid_updatesEntityAndReturnsResponse() {
        UUID id = project.getId();
        ProjectUpdateReq request = new ProjectUpdateReq("Project Gamma", "Updated", LocalDate.of(2024, 1, 11));
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectRepository.existsByNameIgnoreCase("Project Gamma")).thenReturn(false);
        when(taskRepository.findByProjectId(project.getId())).thenReturn(List.of());
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toRes(project)).thenReturn(projectRes);

        ProjectRes result = projectService.update(id, request);

        assertThat(result).isSameAs(projectRes);
        ArgumentCaptor<Project> entityCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectMapper).update(entityCaptor.capture(), org.mockito.ArgumentMatchers.eq(request));
        assertThat(entityCaptor.getValue()).isSameAs(project);
        verify(projectRepository).save(project);
    }

    @Test
    void delete_whenProjectMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> projectService.delete(id))
                .withMessage("Project not found");

        verify(projectRepository, never()).deleteById(id);
    }

    @Test
    void delete_whenProjectExists_deletesEntity() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(true);

        projectService.delete(id);

        verify(projectRepository).deleteById(id);
    }
}
