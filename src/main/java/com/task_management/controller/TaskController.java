package com.task_management.controller;

import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import com.task_management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create task",
            description = "Creates a new task within a project.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created"),
                    @ApiResponse(responseCode = "400", description = "Validation failure"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public TaskRes create(@Valid @RequestBody TaskCreateReq req) {
        return taskService.create(req);
    }

    @GetMapping("/{taskId}")
    @Operation(
            summary = "Get task",
            description = "Retrieves a task by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task found"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public TaskRes get(@Parameter(description = "Task identifier") @PathVariable UUID taskId) {
        return taskService.get(taskId);
    }

    @GetMapping
    @Operation(
            summary = "List project tasks",
            description = "Returns a paginated list of tasks for the specified project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of tasks retrieved"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public Page<TaskRes> listByProject(@Parameter(description = "Project identifier")
                                       @RequestParam("projectId") UUID projectId,
                                       @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return taskService.listInProject(projectId, pageable);
    }

    @PatchMapping("/{taskId}")
    @Operation(
            summary = "Update task",
            description = "Updates the details of a task.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task updated"),
                    @ApiResponse(responseCode = "404", description = "Task not found"),
                    @ApiResponse(responseCode = "400", description = "Validation failure")
            }
    )
    public TaskRes update(@Parameter(description = "Task identifier") @PathVariable UUID taskId,
                          @Valid @RequestBody TaskUpdateReq req) {
        return taskService.update(taskId, req);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete task",
            description = "Deletes the specified task.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public void delete(@Parameter(description = "Task identifier") @PathVariable UUID taskId) {
        taskService.delete(taskId);
    }
}
