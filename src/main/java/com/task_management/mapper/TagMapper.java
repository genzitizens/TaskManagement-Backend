package com.task_management.mapper;

import com.task_management.dto.TagRes;
import com.task_management.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "activity", source = "activity")
    TagRes toRes(Tag entity);
}
