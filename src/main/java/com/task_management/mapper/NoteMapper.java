package com.task_management.mapper;

import com.task_management.dto.NoteRes;
import com.task_management.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(target="projectId", source="project.id")
    @Mapping(target="taskId",    source="task.id")
    NoteRes toRes(Note entity);
}