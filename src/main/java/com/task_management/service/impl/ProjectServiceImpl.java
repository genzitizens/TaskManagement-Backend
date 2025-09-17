package com.task_management.service.impl;


import com.task_management.dto.ProjectCreateReq;
import com.task_management.dto.ProjectRes;
import com.task_management.dto.ProjectUpdateReq;
import com.task_management.entity.Project;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.ProjectMapper;
import com.task_management.repository.ProjectRepository;
import com.task_management.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projects;
    private final ProjectMapper mapper;

    @Override
    public ProjectRes create(ProjectCreateReq req) {
        if (projects.existsByNameIgnoreCase(req.name()))
            throw new BadRequestException("Project name already exists");
        Project p = mapper.toEntity(req);
        return mapper.toRes(projects.save(p));
    }

    @Override
    public ProjectRes get(java.util.UUID id) {
        return mapper.toRes(projects.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found")));
    }

    @Override
    public Page<ProjectRes> list(Pageable pageable) {
        return projects.findAllByOrderByCreatedAtDesc(pageable).map(mapper::toRes);
    }

    @Override
    public ProjectRes update(java.util.UUID id, ProjectUpdateReq req) {
        var p = projects.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
        if (req.name() != null && !p.getName().equalsIgnoreCase(req.name())
                && projects.existsByNameIgnoreCase(req.name()))
            throw new BadRequestException("Project name already exists");
        mapper.update(p, req);
        return mapper.toRes(projects.save(p));
    }

    @Override
    public void delete(java.util.UUID id) {
        if (!projects.existsById(id)) throw new NotFoundException("Project not found");
        projects.deleteById(id);
    }
}
