package com.scnsoft.eldermark.services.carecoordination;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.entity.EncounterNote;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.NoteStatus;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteEventDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteResidentAdmittanceHistoryDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;

@Service
@Transactional(readOnly = true)
public class NoteDetailsServiceImpl implements NoteDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(NoteDetailsServiceImpl.class);

    @Autowired
    private NoteDao noteDao;

    @Override
    public NoteDto getNoteDetails(Long noteId, boolean includeHistory, int timeZoneOffset) {
        Note note = noteDao.findOne(noteId);
        Hibernate.initialize(note);
        if (note instanceof HibernateProxy) {
            note = (Note) ((HibernateProxy) note).getHibernateLazyInitializer().getImplementation();
        }
        return toNoteDto(note, includeHistory ? getHistoryNotes(note) : null, timeZoneOffset);
    }

    private List<Note> getHistoryNotes(Note note) {
        if (note == null || note.getStatus().equals(NoteStatus.CREATED)) {
            return Collections.emptyList();
        }
        return noteDao.getAllByChainIdOrIdAndArchivedIsTrueOrderByLastModifiedDateDescIdDesc(note.getChainId(),
                note.getChainId());
    }

    private static NoteDto toNoteDto(Note note, List<Note> historyNotes, int timeZoneOffset) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setPatientId(note.getResident().getId());
        dto.setType(note.getType().getDisplayName());
        dto.setSubType(new NoteSubTypeDto(note.getSubType().getId(), note.getSubType().getDescription(),
                note.getSubType().getFollowUpCode()));
        if (note.getAdmittanceHistory() != null) {
            dto.setNoteResidentAdmittanceHistoryDto(new NoteResidentAdmittanceHistoryDto(
                    note.getAdmittanceHistory().getId(), note.getAdmittanceHistory().getAdmitDate()));
        } else if (note.getIntakeDate() != null) {
            dto.setNoteResidentAdmittanceHistoryDto(new NoteResidentAdmittanceHistoryDto(0l, note.getIntakeDate()));
        }
        if (note.getEvent() != null) {
            final NoteEventDto noteEventDto = new NoteEventDto();
            noteEventDto.setId(note.getEvent().getId());
            noteEventDto.setDescription(note.getEvent().getEventType().getDescription());
            noteEventDto.setDate(note.getEvent().getEventDatetime());
            dto.setEvent(noteEventDto);
        }
        dto.setStatus(note.getStatus().getDisplayName());
        dto.setLastModifiedDate(note.getLastModifiedDate());
        dto.setPersonSubmittingNote(note.getEmployee().getFullName());
        dto.setRole(note.getEmployee().getCareTeamRole().getName());
        dto.setSubjective(note.getSubjective());
        dto.setObjective(note.getObjective());
        dto.setAssessment(note.getAssessment());
        dto.setPlan(note.getPlan());
        if (!CollectionUtils.isEmpty(historyNotes)) {
            dto.setHistoryNotes(NoteUtil.toRelatedNoteDtoList(historyNotes));
        }

        if (note instanceof EncounterNote) {
            EncounterNote eNote = (EncounterNote) note;
            dto.setClinicianCompletingEncounter(eNote.getClinicianCompletingEncounter());
            dto.setEncouterNoteTypeId(eNote.getEncounterNoteType().getId());
            dto.setEncoutnerNoteType(eNote.getEncounterNoteType().getDescription());

            Date fromTime = getLocalTime(eNote.getEncounterDate(), eNote.getFromTime(), timeZoneOffset);
            Date toTime = getLocalTime(eNote.getEncounterDate(), eNote.getToTime(), timeZoneOffset);

            dto.setEncounterDate(fromTime);
            dto.setFrom(fromTime);
            dto.setTo(toTime);
            populateCalculatedFields(dto, eNote.getFromTime(), eNote.getToTime());
        }

        return dto;
    }

    private static void populateCalculatedFields(NoteDto dto, Date fromTime, Date toTime) {
        try {
            long diffMinutes = (toTime.getTime() - fromTime.getTime()) / (60 * 1000);  
            dto.setTotalTimeSpent(diffMinutes);
            
            long m = (new Double(Math.floor(diffMinutes / 15))).longValue();
            long r = diffMinutes % 15;
            if (r > 7) {
                m += 1;
            }
            int startRange = (int) (m * 15 - 7);
            int endRange = (int) (m * 15 + 7);
            if (startRange < 0) {
                startRange = 0;
            }
            dto.setRange(startRange + " mins - " + endRange + " mins");
            dto.setUnits(m);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static Date getLocalTime(Date date, Date time, int timeZoneOffsetInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        cal.add(Calendar.MINUTE, -timeZoneOffsetInMinutes);
        return cal.getTime();
    }
}
