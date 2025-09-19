package com.task_management.controller;

import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.service.ProjectService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a project",
            description = "Creates a new project with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Project created"),
                    @ApiResponse(responseCode = "400", description = "Validation failure")
            }
    )
    public ProjectRes create(@Valid @RequestBody ProjectCreateReq req) {
        return projectService.create(req);
    }

    @GetMapping("/{projectId}")
    @Operation(
            summary = "Get project",
            description = "Retrieves the details of a project by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project found"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public ProjectRes get(@Parameter(description = "Project identifier") @PathVariable UUID projectId) {
        return projectService.get(projectId);
    }

    @GetMapping
    @Operation(
            summary = "List projects",
            description = "Returns a paginated list of projects ordered by name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of projects retrieved")
            }
    )
    public Page<ProjectRes> list(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return projectService.list(pageable);
    }

    @PatchMapping("/{projectId}")
    @Operation(
        summary = "Update project",
        description = "Updates project details for the given identifier.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Project updated"),
                @ApiResponse(responseCode = "404", description = "Project not found"),
                @ApiResponse(responseCode = "400", description = "Validation failure")
        }
    )
    public ProjectRes update(@Parameter(description = "Project identifier") @PathVariable UUID projectId,
                            @Valid @RequestBody ProjectUpdateReq req) {
        return projectService.update(projectId, req);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete project",
            description = "Removes the project permanently.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Project deleted"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public void delete(@Parameter(description = "Project identifier") @PathVariable UUID projectId) {
        projectService.delete(projectId);
    }
}
