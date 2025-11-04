package com.task_management.service;

import com.task_management.dto.TagCreateReq;
import com.task_management.dto.TagRes;
import com.task_management.dto.TagUpdateReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TagService {
    TagRes create(TagCreateReq req);

    TagRes get(UUID id);

    Page<TagRes> listInProject(UUID projectId, Pageable pageable);

    TagRes update(UUID id, TagUpdateReq req);

    void delete(UUID id);
}
