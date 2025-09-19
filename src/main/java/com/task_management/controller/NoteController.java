package com.task_management.controller;

import com.task_management.dto.NoteCreateReq;
import com.task_management.dto.NoteRes;
import com.task_management.exception.BadRequestException;
import com.task_management.service.NoteService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteRes create(@Valid @RequestBody NoteCreateReq req) {
        return noteService.create(req);
    }

    @GetMapping
    public Page<NoteRes> list(@RequestParam(value = "projectId", required = false) UUID projectId,
                              @RequestParam(value = "taskId", required = false) UUID taskId,
                              @PageableDefault(size = 20) Pageable pageable) {
        boolean hasProject = projectId != null;
        boolean hasTask = taskId != null;
        if (hasProject == hasTask) {
            throw new BadRequestException("Provide either projectId or taskId");
        }
        return hasProject
                ? noteService.listForProject(projectId, pageable)
                : noteService.listForTask(taskId, pageable);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID noteId) {
        noteService.delete(noteId);
    }
}
