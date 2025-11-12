package com.task_management.mapper;

import com.task_management.dto.ActionRes;
import com.task_management.entity.Action;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionMapper {
    @Mapping(target = "taskId", source = "task.id")
    ActionRes toRes(Action entity);
}