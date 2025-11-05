package com.task_management.controller;

import com.task_management.dto.TagCreateReq;
import com.task_management.dto.TagRes;
import com.task_management.dto.TagUpdateReq;
import com.task_management.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tags")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create tag",
            description = "Creates a new tag within a project.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tag created"),
                    @ApiResponse(responseCode = "400", description = "Validation failure"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public TagRes create(@Valid @RequestBody TagCreateReq req) {
        return tagService.create(req);
    }

    @GetMapping("/{tagId}")
    @Operation(
            summary = "Get tag",
            description = "Retrieves a tag by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag found"),
                    @ApiResponse(responseCode = "404", description = "Tag not found")
            }
    )
    public TagRes get(@Parameter(description = "Tag identifier") @PathVariable UUID tagId) {
        return tagService.get(tagId);
    }

    @GetMapping
    @Operation(
            summary = "List project tags",
            description = "Returns a paginated list of tags for the specified project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of tags retrieved"),
                    @ApiResponse(responseCode = "404", description = "Project not found")
            }
    )
    public Page<TagRes> listByProject(@Parameter(description = "Project identifier")
                                      @RequestParam("projectId") UUID projectId,
                                      @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return tagService.listInProject(projectId, pageable);
    }

    @PatchMapping("/{tagId}")
    @Operation(
            summary = "Update tag",
            description = "Updates the details of a tag.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag updated"),
                    @ApiResponse(responseCode = "404", description = "Tag not found"),
                    @ApiResponse(responseCode = "400", description = "Validation failure")
            }
    )
    public TagRes update(@Parameter(description = "Tag identifier") @PathVariable UUID tagId,
                         @Valid @RequestBody TagUpdateReq req) {
        return tagService.update(tagId, req);
    }

    @DeleteMapping("/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete tag",
            description = "Deletes the specified tag.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tag deleted"),
                    @ApiResponse(responseCode = "404", description = "Tag not found")
            }
    )
    public void delete(@Parameter(description = "Tag identifier") @PathVariable UUID tagId) {
        tagService.delete(tagId);
    }
}
