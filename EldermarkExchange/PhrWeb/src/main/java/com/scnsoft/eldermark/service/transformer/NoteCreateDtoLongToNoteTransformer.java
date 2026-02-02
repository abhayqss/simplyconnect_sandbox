package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.services.carecoordination.NoteSubTypeService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.notes.NoteCreateDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class NoteCreateDtoLongToNoteTransformer extends BasePhrService implements BiConverter<NoteCreateDto, Long, Note> {
    private static final Logger logger = LoggerFactory.getLogger(NoteCreateDtoLongToNoteTransformer.class);

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Override
    public Note convert(NoteCreateDto noteCreateDto, Long residentId) {
        if (noteCreateDto == null || residentId == null) {
            return null;
        }
        final Note note = new Note();
        note.setStatus(NoteStatus.CREATED);
        note.setLastModifiedDate(noteCreateDto.getLastModifiedDate());

        note.setEmployee(careTeamSecurityUtils.getCurrentEmployeeOrThrow());

        Resident resident = null;
        if (noteCreateDto.getEventId() == null) {
            note.setType(NoteType.PATIENT_NOTE);
            resident = residentDao.getResident(residentId);
            note.setResident(resident);
            note.setNoteResidents(Collections.singletonList(resident));
        } else {
            note.setType(NoteType.EVENT_NOTE);
            final Event event = eventDao.get(noteCreateDto.getEventId());
            note.setEvent(event);
            resident = residentDao.getResident(event.getResident().getId());
            note.setNoteResidents(Collections.singletonList(resident));
            note.setResident(resident);
        }
        note.setArchived(false);
        note.setSubjective(noteCreateDto.getSubjective());
        note.setObjective(noteCreateDto.getObjective());
        note.setAssessment(noteCreateDto.getAssessment());
        note.setPlan(noteCreateDto.getPlan());

        note.setSubType(noteSubTypeService.getById(noteCreateDto.getSubTypeId()));

        if (note.getSubType().getFollowUpCode() != null) {
            if (noteCreateDto.getAdmitDateId() == null) {
                throw new PhrException(PhrExceptionType.NO_ADMIT_DATE_FOR_FOLLOW_UP_NOTE);
            }

            if (note.getSubType().getFollowUpCode() != null &&
                    noteSubTypeService.getTakenAdmitIntakeHistoryIdForSubType(note.getResident().getId(),
                            note.getSubType().getFollowUpCode()).contains(noteCreateDto.getAdmitDateId())) {
                throw new PhrException(PhrExceptionType.ALREADY_TAKEN_ADMIT_RECORD,
                        "'" + note.getSubType().getDescription() + "' note has been already created for this admit/intake date.");
            }

            if (noteCreateDto.getAdmitDateId() == 0) {
                note.setIntakeDate(resident.getIntakeDate());
            } else {
                final AdmittanceHistory admittanceHistory = admittanceHistoryDao.get(noteCreateDto.getAdmitDateId());
                if (!residentId.equals(admittanceHistory.getResident().getId())) {
                    logger.warn("Resident with id = {} doesn't have attendance record with id = {}", note.getResident().getId(), admittanceHistory.getId());
                    throw new PhrException(PhrExceptionType.NOT_ASSOCIATED_ADMIT_RECORD);
                }
                note.setAdmittanceHistory(admittanceHistory);
            }
        }
        return note;
    }
}
