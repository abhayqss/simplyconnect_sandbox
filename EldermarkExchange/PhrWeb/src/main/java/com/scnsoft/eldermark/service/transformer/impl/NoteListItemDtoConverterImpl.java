package com.scnsoft.eldermark.service.transformer.impl;

import com.scnsoft.eldermark.dao.phr.NoteReadStatusDao;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.service.transformer.NoteListItemDtoConverter;
import com.scnsoft.eldermark.web.entity.notes.NoteListItemDto;
import com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NoteListItemDtoConverterImpl implements NoteListItemDtoConverter {

    private final Logger logger = LoggerFactory.getLogger(NoteListItemDtoConverterImpl.class);

    @Autowired
    private Converter<NoteSubType, NoteSubTypeDto> noteSubTypeDtoConverter;

    @Autowired
    private NoteReadStatusDao noteReadStatusDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    private final Map<Long, Boolean> readMap = new HashMap<>();

    @Override
    public NoteListItemDto convert(Note note) {
        if (note == null) {
            return null;
        }
        NoteListItemDto noteListItemDto = new NoteListItemDto();
        noteListItemDto.setId(note.getId());
        noteListItemDto.setText(generateNoteText(note));
        noteListItemDto.setResidentName(note.getResident().getFullName());
        noteListItemDto.setStatus(note.getStatus());
        noteListItemDto.setType(note.getType());
        noteListItemDto.setSubType(noteSubTypeDtoConverter.convert(note.getSubType()));
        noteListItemDto.setLastModifiedDate(note.getLastModifiedDate());
        noteListItemDto.setDataSource(DataSourceService.transform(note.getEmployee().getDatabase(), note.getResident().getId()));
        noteListItemDto.setUnread(BooleanUtils.isNotTrue(getWasRead(note.getId())));
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

    private Boolean getWasRead(Long noteId) {
        if (readMap.containsKey(noteId)) {
            return readMap.get(noteId);
        }
        return getWasReadFromDao(noteId);
    }

    private Boolean getWasReadFromDao(Long noteId) {
        return noteReadStatusDao.getWasReadByUserIdAndNoteId(careTeamSecurityUtils.getCurrentUser().getId(), noteId);
    }

    public NoteListItemDtoConverter addToReadMap(Map<Long, Boolean> readMap) {
        this.readMap.putAll(readMap);
        return this;
    }
}
