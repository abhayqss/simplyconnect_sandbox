package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.web.entity.NoteListItemDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author sparuchnik
 * Created on 4/11/2018.s
 */
@Service
@Transactional
public class NotesService {
    Logger logger = Logger.getLogger(NotesService.class.getName());

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private ResidentsService residentsService;

    public Page<NoteListItemDto> getListNotes(Long residentId, Pageable pageRequest) {
        residentsService.checkAccessOrThrow(residentId);
        Page<Note> notes =  noteDao.getAllByResident_IdInAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(getMatchedResidentIds(residentId), pageRequest);
        return new PageImpl<>(toNoteListDto(notes.getContent()), pageRequest, notes.getTotalElements());
    }

    public Long getListNotesCount(Long residentId) {
        residentsService.checkAccessOrThrow(residentId);
        return noteDao.countByResident_IdInAndArchivedIsFalse(getMatchedResidentIds(residentId));
    }

    Set<Long> getMatchedResidentIds(Long patientId) {
        final Set<Long> mergedFilterResidentsIds = new HashSet<>();
        mergedFilterResidentsIds.add(patientId);
        mergedFilterResidentsIds.addAll(mpiService.listMergedResidents(patientId));
        return mergedFilterResidentsIds;
    }

    private List<NoteListItemDto> toNoteListDto(List<Note> notes) {
        final List<NoteListItemDto> dtos = new ArrayList<>(notes.size());
        for (Note source : notes) {
            dtos.add(toNoteListItemDto(source));
        }
        return dtos;
    }

    private NoteListItemDto toNoteListItemDto(Note source) {
        NoteListItemDto noteListItemDto = new NoteListItemDto();
        noteListItemDto.setId(source.getId());
        noteListItemDto.setText(generateNoteText(source));
        noteListItemDto.setStatus(source.getStatus());
        noteListItemDto.setLastModifiedDate(source.getLastModifiedDate());
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
        logger.warning("Note with id = " + source.getId() + " has no text in all fields.");
        return StringUtils.EMPTY;
    }
}
