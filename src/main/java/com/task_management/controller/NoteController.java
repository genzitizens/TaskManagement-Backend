package com.task_management.controller;

import com.task_management.dto.NoteCreateReq;
import com.task_management.dto.NoteRes;
import com.task_management.dto.NoteUpdateReq;
import com.task_management.exception.BadRequestException;
import com.task_management.service.NoteService;
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
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notes")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create note",
            description = "Creates a new note linked to a project or a task.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Note created"),
                    @ApiResponse(responseCode = "400", description = "Validation failure or missing association")
            }
    )
    public NoteRes create(@Valid @RequestBody NoteCreateReq req) {
        return noteService.create(req);
    }

    @GetMapping
    @Operation(
            summary = "List notes",
            description = "Lists notes for a project or a task. Exactly one identifier must be provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of notes retrieved"),
                    @ApiResponse(responseCode = "400", description = "Both identifiers provided or missing"),
                    @ApiResponse(responseCode = "404", description = "Project or task not found")
            }
    )
    public Page<NoteRes> list(@Parameter(description = "Project identifier", required = false)
                              @RequestParam(value = "projectId", required = false) UUID projectId,
                              @Parameter(description = "Task identifier", required = false)
                              @RequestParam(value = "taskId", required = false) UUID taskId,
                              @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        boolean hasProject = projectId != null;
        boolean hasTask = taskId != null;
        if (hasProject == hasTask) {
            throw new BadRequestException("Provide either projectId or taskId");
        }
        return hasProject
                ? noteService.listForProject(projectId, pageable)
                : noteService.listForTask(taskId, pageable);
    }

    @PatchMapping("/{noteId}")
    @Operation(
            summary = "Update note",
            description = "Updates the content of a note.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note updated"),
                    @ApiResponse(responseCode = "404", description = "Note not found"),
                    @ApiResponse(responseCode = "400", description = "Validation failure")
            }
    )
    public NoteRes update(@Parameter(description = "Note identifier") @PathVariable UUID noteId,
                          @Valid @RequestBody NoteUpdateReq req) {
        return noteService.update(noteId, req);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete note",
            description = "Deletes a note by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Note deleted"),
                    @ApiResponse(responseCode = "404", description = "Note not found")
            }
    )
    public void delete(@Parameter(description = "Note identifier") @PathVariable UUID noteId) {
        noteService.delete(noteId);
    }
}
