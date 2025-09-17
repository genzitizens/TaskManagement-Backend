package com.task_management.service;

import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
public interface ProjectService {
    ProjectRes create(ProjectCreateReq req);
    ProjectRes get(UUID id);
    Page<ProjectRes> list(Pageable pageable);
    ProjectRes update(UUID id, ProjectUpdateReq req);
    void delete(UUID id);
}
