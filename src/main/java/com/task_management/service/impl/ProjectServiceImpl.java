package com.task_management.service.impl;


import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.entity.Project;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.ProjectMapper;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.ProjectService;
import com.task_management.service.TaskScheduleCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final ProjectMapper mapper;
    private final TaskScheduleCalculator scheduleCalculator;

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
        var originalStartDate = p.getStartDate();
        mapper.update(p, req);
        if (req.startDate() != null && !req.startDate().equals(originalStartDate)) {
            recalculateTaskSchedule(p);
        }
        return mapper.toRes(projects.save(p));
    }

    @Override
    public void delete(UUID id) {
        if (!projects.existsById(id)) throw new NotFoundException("Project not found");
        projects.deleteById(id);
    }

    private void recalculateTaskSchedule(Project project) {
        var projectTasks = tasks.findByProjectId(project.getId());
        if (projectTasks.isEmpty()) {
            return;
        }
        for (var task : projectTasks) {
            task.setProject(project);
            scheduleCalculator.applyScheduleDays(task);
        }
        tasks.saveAll(projectTasks);
    }
}
