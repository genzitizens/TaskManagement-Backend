package com.task_management.mapper;

import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.entity.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toEntity(ProjectCreateReq req);

    ProjectRes toRes(Project entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Project entity, ProjectUpdateReq req);
}