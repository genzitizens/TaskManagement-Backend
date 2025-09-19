package com.task_management.controller;

import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.service.ProjectService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectRes create(@Valid @RequestBody ProjectCreateReq req) {
        return projectService.create(req);
    }

    @GetMapping("/{projectId}")
    public ProjectRes get(@PathVariable UUID projectId) {
        return projectService.get(projectId);
    }

    @GetMapping
    public Page<ProjectRes> list(@PageableDefault(size = 20) Pageable pageable) {
        return projectService.list(pageable);
    }

    @PatchMapping("/{projectId}")
    public ProjectRes update(@PathVariable UUID projectId, @Valid @RequestBody ProjectUpdateReq req) {
        return projectService.update(projectId, req);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId) {
        projectService.delete(projectId);
    }
}
