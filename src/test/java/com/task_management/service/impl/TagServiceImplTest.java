package com.task_management.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.task_management.dto.TagCreateReq;
import com.task_management.dto.TagRes;
import com.task_management.dto.TagUpdateReq;
import com.task_management.entity.Project;
import com.task_management.entity.Tag;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.TagMapper;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TagRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private Project project;
    private Tag tag;
    private TagRes tagRes;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Project");
        project.setStartDate(LocalDate.of(2024, 1, 1));

        tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setProject(project);
        tag.setTitle("Tag");
        tag.setDescription("Description");
        tag.setActivity(true);
        tag.setDuration(60);
        tag.setStartAt(Instant.parse("2024-01-09T12:00:00Z"));
        tag.setEndAt(Instant.parse("2024-01-10T12:00:00Z"));
        tag.setStartDay(9);
        tag.setEndDay(10);

        tagRes = new TagRes(
                tag.getId(),
                project.getId(),
                tag.getTitle(),
                tag.getDescription(),
                tag.isActivity(),
                tag.getDuration(),
                tag.getStartAt(),
                tag.getEndAt(),
                tag.getStartDay(),
                tag.getEndDay(),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z")
        );
    }

    @Test
    void create_whenProjectMissing_throwsNotFound() {
        UUID projectId = UUID.randomUUID();
        TagCreateReq request = new TagCreateReq(projectId, "Title", null, false, 30, Instant.now(), Instant.now());
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tagService.create(request))
                .withMessage("Project not found");
    }

    @Test
    void create_whenEndAtMissing_throwsBadRequest() {
        UUID projectId = project.getId();
        TagCreateReq request = new TagCreateReq(projectId, "Title", null, false, 30, Instant.now(), null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.create(request))
                .withMessage("endAt is required");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void create_whenStartAtMissing_throwsBadRequest() {
        UUID projectId = project.getId();
        TagCreateReq request = new TagCreateReq(projectId, "Title", null, false, 30, null, Instant.now());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.create(request))
                .withMessage("startAt is required");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void create_whenTitleBlankAfterTrim_throwsBadRequest() {
        UUID projectId = project.getId();
        TagCreateReq request = new TagCreateReq(projectId, "   ", null, false, 30, Instant.now(), Instant.now());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.create(request))
                .withMessage("Tag title required");
    }

    @Test
    void create_whenValid_savesTag() {
        UUID projectId = project.getId();
        Instant startAt = Instant.parse("2024-01-01T00:00:00Z");
        Instant endAt = Instant.parse("2024-02-01T00:00:00Z");
        TagCreateReq request = new TagCreateReq(projectId, "  Important Tag  ", "Desc", true, 30, startAt, endAt);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toRes(tag)).thenReturn(tagRes);

        TagRes result = tagService.create(request);

        assertThat(result).isSameAs(tagRes);
        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(captor.capture());
        Tag saved = captor.getValue();
        assertThat(saved.getProject()).isSameAs(project);
        assertThat(saved.getTitle()).isEqualTo("Important Tag");
        assertThat(saved.getDescription()).isEqualTo("Desc");
        assertThat(saved.isActivity()).isTrue();
        assertThat(saved.getDuration()).isEqualTo(30);
        assertThat(saved.getStartAt()).isEqualTo(startAt);
        assertThat(saved.getEndAt()).isEqualTo(endAt);
        assertThat(saved.getStartDay()).isEqualTo(0);
        assertThat(saved.getEndDay()).isEqualTo(31);
    }

    @Test
    void create_whenStartAtSharesCalendarDayWithOffset_savesWithZeroStartDay() {
        UUID projectId = project.getId();
        Instant startAt = project.getStartDate().atStartOfDay().atOffset(ZoneOffset.ofHours(5)).toInstant();
        Instant endAt = project.getStartDate().plusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHours(5)).toInstant();
        TagCreateReq request = new TagCreateReq(projectId, "Offset Tag", null, false, 45, startAt, endAt);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tagMapper.toRes(any(Tag.class))).thenReturn(tagRes);

        TagRes result = tagService.create(request);

        assertThat(result).isSameAs(tagRes);
        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(captor.capture());
        Tag saved = captor.getValue();
        assertThat(saved.getStartAt()).isEqualTo(startAt);
        assertThat(saved.getStartDay()).isZero();
    }

    @Test
    void create_whenStartAtBeforeProjectStart_throwsBadRequest() {
        UUID projectId = project.getId();
        Instant startAt = project.getStartDate().minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endAt = startAt.plus(Duration.ofDays(2));
        TagCreateReq request = new TagCreateReq(projectId, "Early", null, false, 30, startAt, endAt);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.create(request))
                .withMessage("startAt cannot be before the project start date");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void get_whenTagExists_returnsResponse() {
        UUID tagId = tag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagMapper.toRes(tag)).thenReturn(tagRes);

        TagRes result = tagService.get(tagId);

        assertThat(result).isSameAs(tagRes);
    }

    @Test
    void get_whenTagMissing_throwsNotFound() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tagService.get(tagId))
                .withMessage("Tag not found");
    }

    @Test
    void listInProject_whenProjectMissing_throwsNotFound() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tagService.listInProject(projectId, pageable))
                .withMessage("Project not found");
    }

    @Test
    void listInProject_returnsMappedPage() {
        UUID projectId = project.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> page = new PageImpl<>(List.of(tag), pageable, 1);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(tagRepository.findByProjectIdOrderByEndAtAsc(projectId, pageable)).thenReturn(page);
        when(tagMapper.toRes(tag)).thenReturn(tagRes);

        Page<TagRes> result = tagService.listInProject(projectId, pageable);

        assertThat(result.getContent()).containsExactly(tagRes);
    }

    @Test
    void update_whenTagMissing_throwsNotFound() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tagService.update(tagId, new TagUpdateReq(null, null, null, null, null, null)))
                .withMessage("Tag not found");
    }

    @Test
    void update_whenTitleBecomesBlank_throwsBadRequest() {
        UUID tagId = tag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.update(tagId, new TagUpdateReq("   ", null, null, null, null, null)))
                .withMessage("Tag title cannot be blank");
    }

    @Test
    void update_whenRequiredFieldsMissingAfterUpdate_throwsBadRequest() {
        Tag existing = new Tag();
        existing.setId(UUID.randomUUID());
        existing.setProject(project);
        existing.setTitle("Title");
        when(tagRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> tagService.update(existing.getId(), new TagUpdateReq(null, null, null, null, null, null)))
                .withMessage("startAt is required");
    }

    @Test
    void update_whenValid_updatesAndReturnsResponse() {
        UUID tagId = tag.getId();
        TagUpdateReq request = new TagUpdateReq("Updated", "New", false, 40,
                Instant.parse("2024-01-11T00:00:00Z"),
                Instant.parse("2024-01-12T00:00:00Z"));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toRes(tag)).thenReturn(tagRes);

        TagRes result = tagService.update(tagId, request);

        assertThat(result).isSameAs(tagRes);
        verify(tagRepository).save(tag);
    }

    @Test
    void delete_whenTagMissing_throwsNotFound() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.existsById(tagId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tagService.delete(tagId))
                .withMessage("Tag not found");

        verify(tagRepository, never()).deleteById(tagId);
    }

    @Test
    void delete_whenTagExists_deletesEntity() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.existsById(tagId)).thenReturn(true);

        tagService.delete(tagId);

        verify(tagRepository).deleteById(tagId);
    }
}
