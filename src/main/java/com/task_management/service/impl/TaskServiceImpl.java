package com.task_management.service.impl;

import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import com.task_management.entity.Task;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.TaskMapper;
import com.task_management.monitoring.TaskMetrics;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.TaskScheduleCalculator;
import com.task_management.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final TaskMapper mapper;
    private final TaskMetrics metrics;
    private final TaskScheduleCalculator scheduleCalculator;

    @Override
    public TaskRes create(TaskCreateReq req) {
        var project = projects.findById(req.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found"));
        if (req.startAt() == null) throw new BadRequestException("startAt is required");
        if (req.endAt() == null) throw new BadRequestException("endAt is required");

        var t = new Task();
        t.setProject(project);
        t.setTitle(req.title().trim());
        if (t.getTitle().isBlank()) throw new BadRequestException("Task title required");
        t.setDescription(req.description());
        t.setActivity(req.activity());
        t.setDuration(req.duration());
        t.setStartAt(req.startAt());
        t.setEndAt(req.endAt());
        scheduleCalculator.applyScheduleDays(t);

        var savedTask = tasks.save(t);
        metrics.incrementCreated();
        return mapper.toRes(savedTask);
    }

    @Override
    public TaskRes get(java.util.UUID id) {
        return mapper.toRes(tasks.findById(id).orElseThrow(() -> new NotFoundException("Task not found")));
    }

    @Override
    public Page<TaskRes> listInProject(java.util.UUID projectId, Pageable pageable) {
        if (!projects.existsById(projectId)) throw new NotFoundException("Project not found");
        return tasks.findByProjectIdOrderByEndAtAsc(projectId, pageable).map(mapper::toRes);
    }

    @Override
    public TaskRes update(java.util.UUID id, TaskUpdateReq req) {
        var t = tasks.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        if (req.title() != null) {
            String v = req.title().trim();
            if (v.isBlank()) throw new BadRequestException("Task title cannot be blank");
            t.setTitle(v);
        }
        if (req.description() != null) t.setDescription(req.description());
        if (req.activity() != null) t.setActivity(req.activity());
        if (req.duration() != null) t.setDuration(req.duration());
        if (req.startAt() != null) t.setStartAt(req.startAt());
        if (req.endAt() != null) t.setEndAt(req.endAt());
        if (t.getStartAt() == null) throw new BadRequestException("startAt is required");
        if (t.getEndAt() == null) throw new BadRequestException("endAt is required");
        if (t.getDuration() == null) throw new BadRequestException("duration is required");
        scheduleCalculator.applyScheduleDays(t);
        var updatedTask = tasks.save(t);
        metrics.incrementUpdated();
        return mapper.toRes(updatedTask);
    }

    @Override
    public void delete(java.util.UUID id) {
        if (!tasks.existsById(id)) throw new NotFoundException("Task not found");
        tasks.deleteById(id);
        metrics.incrementDeleted();
    }

}
