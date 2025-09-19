package com.task_management.controller;

import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import com.task_management.service.TaskService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskRes create(@Valid @RequestBody TaskCreateReq req) {
        return taskService.create(req);
    }

    @GetMapping("/{taskId}")
    public TaskRes get(@PathVariable UUID taskId) {
        return taskService.get(taskId);
    }

    @GetMapping
    public Page<TaskRes> listByProject(@RequestParam("projectId") UUID projectId,
                                       @PageableDefault(size = 20) Pageable pageable) {
        return taskService.listInProject(projectId, pageable);
    }

    @PatchMapping("/{taskId}")
    public TaskRes update(@PathVariable UUID taskId, @Valid @RequestBody TaskUpdateReq req) {
        return taskService.update(taskId, req);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID taskId) {
        taskService.delete(taskId);
    }
}
