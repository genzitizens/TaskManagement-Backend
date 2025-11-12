package com.task_management.controller;

import com.task_management.dto.ActionCreateReq;
import com.task_management.dto.ActionRes;
import com.task_management.dto.ActionUpdateReq;
import com.task_management.service.ActionService;
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
@RequestMapping("/api/actions")
@RequiredArgsConstructor
@Tag(name = "Actions")
public class ActionController {

    private final ActionService actionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create action",
            description = "Creates a new action within a task.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Action created"),
                    @ApiResponse(responseCode = "400", description = "Validation failure"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public ActionRes create(@Valid @RequestBody ActionCreateReq req) {
        return actionService.create(req);
    }

    @GetMapping("/{actionId}")
    @Operation(
            summary = "Get action",
            description = "Retrieves an action by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Action found"),
                    @ApiResponse(responseCode = "404", description = "Action not found")
            }
    )
    public ActionRes get(@Parameter(description = "Action identifier") @PathVariable UUID actionId) {
        return actionService.get(actionId);
    }

    @GetMapping
    @Operation(
            summary = "List task actions",
            description = "Returns a paginated list of actions for the specified task.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of actions retrieved"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public Page<ActionRes> listByTask(@Parameter(description = "Task identifier")
                                      @RequestParam("taskId") UUID taskId,
                                      @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return actionService.listInTask(taskId, pageable);
    }

    @PatchMapping("/{actionId}")
    @Operation(
            summary = "Update action",
            description = "Updates the details of an action.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Action updated"),
                    @ApiResponse(responseCode = "404", description = "Action not found"),
                    @ApiResponse(responseCode = "400", description = "Validation failure")
            }
    )
    public ActionRes update(@Parameter(description = "Action identifier") @PathVariable UUID actionId,
                            @Valid @RequestBody ActionUpdateReq req) {
        return actionService.update(actionId, req);
    }

    @DeleteMapping("/{actionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete action",
            description = "Deletes the specified action.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Action deleted"),
                    @ApiResponse(responseCode = "404", description = "Action not found")
            }
    )
    public void delete(@Parameter(description = "Action identifier") @PathVariable UUID actionId) {
        actionService.delete(actionId);
    }
}