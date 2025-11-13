package com.task_management.service.impl;


import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectImportReq;
import com.task_management.dto.ProjectImportRes;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.entity.Note;
import com.task_management.entity.Project;
import com.task_management.entity.Tag;
import com.task_management.entity.Task;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.ProjectMapper;
import com.task_management.repository.NoteRepository;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TagRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final NoteRepository notes;
    private final TagRepository tags;
    private final ProjectMapper mapper;

    @Override
    public ProjectRes create(ProjectCreateReq req) {
        if (projects.existsByNameIgnoreCase(req.name()))
            throw new BadRequestException("Project name already exists");
        Project p = mapper.toEntity(req);
        return mapper.toRes(projects.save(p));
    }

    @Override
    public ProjectRes get(UUID id) {
        return mapper.toRes(projects.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found")));
    }

    @Override
    public Page<ProjectRes> list(Pageable pageable) {
        return projects.findAllByOrderByCreatedAtDesc(pageable).map(mapper::toRes);
    }

    @Override
    public ProjectRes update(UUID id, ProjectUpdateReq req) {
        var p = projects.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
        if (req.name() != null && !p.getName().equalsIgnoreCase(req.name())
                && projects.existsByNameIgnoreCase(req.name()))
            throw new BadRequestException("Project name already exists");
        mapper.update(p, req);
        return mapper.toRes(projects.save(p));
    }

    @Override
    public ProjectImportRes importProject(ProjectImportReq req) {
        // Validate source project exists
        Project sourceProject = projects.findById(req.sourceProjectId())
                .orElseThrow(() -> new NotFoundException("Source project not found"));
        
        // Check if new project name already exists
        if (projects.existsByNameIgnoreCase(req.newProjectName())) {
            throw new BadRequestException("Target project name already exists");
        }

        // Create new project
        Project newProject = Project.builder()
                .name(req.newProjectName())
                .description(req.description() != null ? req.description() : sourceProject.getDescription())
                .startDate(sourceProject.getStartDate())
                .build();
        
        newProject = projects.save(newProject);

        int importedTasksCount = 0;
        int importedNotesCount = 0;
        int importedTagsCount = 0;

        // Import tasks if requested
        if (req.importTasks()) {
            List<Task> sourceTasks = tasks.findByProjectId(req.sourceProjectId());
            for (Task sourceTask : sourceTasks) {
                Task newTask = Task.builder()
                        .project(newProject)
                        .title(sourceTask.getTitle())
                        .description(sourceTask.getDescription())
                        .activity(sourceTask.isActivity())
                        .duration(sourceTask.getDuration())
                        .startAt(sourceTask.getStartAt())
                        .endAt(sourceTask.getEndAt())
                        .startDay(sourceTask.getStartDay())
                        .endDay(sourceTask.getEndDay())
                        .color(sourceTask.getColor())
                        .build();
                tasks.save(newTask);
                importedTasksCount++;
            }
        }

        // Import tags if requested
        if (req.importTags()) {
            List<Tag> sourceTags = tags.findByProjectId(req.sourceProjectId());
            for (Tag sourceTag : sourceTags) {
                Tag newTag = Tag.builder()
                        .project(newProject)
                        .title(sourceTag.getTitle())
                        .description(sourceTag.getDescription())
                        .activity(sourceTag.isActivity())
                        .duration(sourceTag.getDuration())
                        .startAt(sourceTag.getStartAt())
                        .endAt(sourceTag.getEndAt())
                        .startDay(sourceTag.getStartDay())
                        .endDay(sourceTag.getEndDay())
                        .color(sourceTag.getColor())
                        .build();
                tags.save(newTag);
                importedTagsCount++;
            }
        }

        // Import notes if requested
        if (req.importNotes()) {
            List<Note> sourceNotes = notes.findByProjectId(req.sourceProjectId());
            for (Note sourceNote : sourceNotes) {
                Note newNote = Note.builder()
                        .project(newProject)
                        .body(sourceNote.getBody())
                        .build();
                notes.save(newNote);
                importedNotesCount++;
            }
        }

        return new ProjectImportRes(
                newProject.getId(),
                newProject.getName(),
                importedTasksCount,
                importedNotesCount,
                importedTagsCount,
                String.format("Successfully imported project '%s' with %d tasks, %d notes, and %d tags",
                      newProject.getName(), importedTasksCount, importedNotesCount, importedTagsCount)
        );
    }

    @Override
    public void delete(UUID id) {
        if (!projects.existsById(id)) throw new NotFoundException("Project not found");
        projects.deleteById(id);
    }
}
