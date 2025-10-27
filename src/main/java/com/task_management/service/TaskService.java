package com.task_management.service;

import com.task_management.dto.TaskCreateReq;
import com.task_management.dto.TaskRes;
import com.task_management.dto.TaskUpdateReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TaskService {
    TaskRes create(TaskCreateReq req);
    TaskRes get(UUID id);
    Page<TaskRes> listInProject(UUID projectId, Pageable pageable);
    TaskRes update(UUID id, TaskUpdateReq req);
    void delete(UUID id);
}