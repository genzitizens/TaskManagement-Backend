package com.task_management.dto;

import java.util.UUID;

public record ProjectImportRes(
        UUID newProjectId,
        String newProjectName,
        int importedTasksCount,
        int importedNotesCount,
        int importedTagsCount,
        int importedActionsCount,
        String message
) {
}