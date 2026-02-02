package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.NoteListItemDto;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dao.specification.NoteSpecificationGenerator;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotesServiceImpl implements NotesService {
    private static final Logger logger = LoggerFactory.getLogger(NotesServiceImpl.class);

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private NoteSpecificationGenerator noteSpecifications;

    @Autowired
    private ResidentsService residentsService;

    @Override
    public Page<NoteListItemDto> getListNotes(Long residentId, Pageable pageRequest) {
        residentsService.checkAccessOrThrow(residentId);
        var byClientAndMerged = noteSpecifications.byClientIdAndMerged(residentId);
        var isUnarchived = noteSpecifications.isUnarchived();
        pageRequest = PaginationUtils.sortByDefault(pageRequest, PaginationUtils.historySort());

        var notes = noteDao.findAll(byClientAndMerged.and(isUnarchived), pageRequest);
        return notes.map(this::toNoteListItemDto);
    }

    @Override
    public Long getListNotesCount(Long residentId) {
        residentsService.checkAccessOrThrow(residentId);
        var byClientAndMerged = noteSpecifications.byClientIdAndMerged(residentId);
        var isUnarchived = noteSpecifications.isUnarchived();
        return noteDao.count(byClientAndMerged.and(isUnarchived));
    }

    private NoteListItemDto toNoteListItemDto(Note source) {
        NoteListItemDto noteListItemDto = new NoteListItemDto();
        noteListItemDto.setId(source.getId());
        noteListItemDto.setText(generateNoteText(source));
        noteListItemDto.setStatus(source.getAuditableStatus());
        noteListItemDto.setLastModifiedDate(DateTimeUtils.toDate(source.getLastModifiedDate()));
        return noteListItemDto;
    }

    private String generateNoteText(Note source) {
        if (StringUtils.isNotEmpty(source.getSubjective())) {
            return source.getSubjective();
        }

        if (StringUtils.isNotEmpty(source.getObjective())) {
            return source.getObjective();
        }

        if (StringUtils.isNotEmpty(source.getAssessment())) {
            return source.getAssessment();
        }

        if (StringUtils.isNotEmpty(source.getPlan())) {
            return source.getPlan();
        }

        logger.warn("Note with id = {} has no text in all fields.", source.getId());
        return StringUtils.EMPTY;
    }
}
