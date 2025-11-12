package com.task_management.service;

import com.task_management.dto.ActionCreateReq;
import com.task_management.dto.ActionRes;
import com.task_management.dto.ActionUpdateReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ActionService {
    ActionRes create(ActionCreateReq req);
    ActionRes get(UUID id);
    Page<ActionRes> listInTask(UUID taskId, Pageable pageable);
    ActionRes update(UUID id, ActionUpdateReq req);
    void delete(UUID id);
}