package com.task_management.mapper;

import com.task_management.dto.TaskRes;
import com.task_management.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "activity", source = "activity")
    TaskRes toRes(Task entity);
}
