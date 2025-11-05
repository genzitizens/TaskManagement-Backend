package com.task_management.service.impl;

import com.task_management.dto.TagCreateReq;
import com.task_management.dto.TagRes;
import com.task_management.dto.TagUpdateReq;
import com.task_management.entity.Tag;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.TagMapper;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TagRepository;
import com.task_management.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tags;
    private final ProjectRepository projects;
    private final TagMapper mapper;

    @Override
    public TagRes create(TagCreateReq req) {
        var project = projects.findById(req.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found"));

        if (req.startAt() == null) throw new BadRequestException("startAt is required");
        if (req.endAt() == null) throw new BadRequestException("endAt is required");

        var tag = new Tag();
        tag.setProject(project);
        tag.setTitle(req.title().trim());
        if (tag.getTitle().isBlank()) throw new BadRequestException("Tag title required");
        tag.setDescription(req.description());
        tag.setActivity(req.activity());
        tag.setDuration(req.duration());
        tag.setStartAt(req.startAt());
        tag.setEndAt(req.endAt());

        applyScheduleDays(tag);

        return mapper.toRes(tags.save(tag));
    }

    @Override
    public TagRes get(java.util.UUID id) {
        return mapper.toRes(tags.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found")));
    }

    @Override
    public Page<TagRes> listInProject(java.util.UUID projectId, Pageable pageable) {
        if (!projects.existsById(projectId)) throw new NotFoundException("Project not found");
        return tags.findByProjectIdOrderByEndAtAsc(projectId, pageable)
                .map(mapper::toRes);
    }

    @Override
    public TagRes update(java.util.UUID id, TagUpdateReq req) {
        var tag = tags.findById(id).orElseThrow(() -> new NotFoundException("Tag not found"));

        if (req.title() != null) {
            String title = req.title().trim();
            if (title.isBlank()) throw new BadRequestException("Tag title cannot be blank");
            tag.setTitle(title);
        }

        if (req.description() != null) tag.setDescription(req.description());
        if (req.activity() != null) tag.setActivity(req.activity());
        if (req.duration() != null) tag.setDuration(req.duration());
        if (req.startAt() != null) tag.setStartAt(req.startAt());
        if (req.endAt() != null) tag.setEndAt(req.endAt());

        if (tag.getStartAt() == null) throw new BadRequestException("startAt is required");
        if (tag.getEndAt() == null) throw new BadRequestException("endAt is required");
        if (tag.getDuration() == null) throw new BadRequestException("duration is required");

        applyScheduleDays(tag);

        return mapper.toRes(tags.save(tag));
    }

    @Override
    public void delete(java.util.UUID id) {
        if (!tags.existsById(id)) throw new NotFoundException("Tag not found");
        tags.deleteById(id);
    }

    private void applyScheduleDays(Tag tag) {
        var project = tag.getProject();
        if (project == null || project.getStartDate() == null) {
            throw new BadRequestException("Project start date is required");
        }

        var projectStart = project.getStartDate();
        var startDate = tag.getStartAt().atZone(ZoneOffset.UTC).toLocalDate();
        var endDate = tag.getEndAt().atZone(ZoneOffset.UTC).toLocalDate();

        int startDay = Math.toIntExact(ChronoUnit.DAYS.between(projectStart, startDate));
        if (startDay < 0) {
            throw new BadRequestException("startAt cannot be before the project start date");
        }

        int endDay = Math.toIntExact(ChronoUnit.DAYS.between(projectStart, endDate));
        if (endDay < startDay) {
            throw new BadRequestException("endAt cannot be before startAt");
        }

        tag.setStartDay(startDay);
        tag.setEndDay(endDay);
    }
}
