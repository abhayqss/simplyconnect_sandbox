package com.scnsoft.eldermark.converter.note;

import com.scnsoft.eldermark.dto.NoteViewData;
import com.scnsoft.eldermark.dto.notification.note.NoteClientProgramViewData;
import com.scnsoft.eldermark.dto.notification.note.NoteEncounterViewData;
import com.scnsoft.eldermark.dto.notification.note.NoteServiceStatusCheckViewData;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.note.*;
import com.scnsoft.eldermark.service.security.ServicePlanSecurityService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional(propagation = Propagation.SUPPORTS)
public abstract class NoteViewDataConverter<E extends NoteEncounterViewData, SSC extends NoteServiceStatusCheckViewData, CP extends NoteClientProgramViewData, T extends NoteViewData<E, SSC, CP>> implements Converter<Note, T> {

    @Autowired
    private ServicePlanSecurityService servicePlanSecurityService;

    @Override
    public T convert(Note note) {
        var target = create();

        fill(note, target);

        return target;
    }

    protected abstract T create();

    protected void fill(Note source, T target) {
        if (NoteType.GROUP_NOTE.equals(source.getType())) {
            fillGroupNoteClients(source, target);
        } else {
            fillNonGroupNoteClient(source, target);
        }

        target.setNoteName(source.getNoteName());

        target.setTypeTitle(source.getType().getDisplayName());
        target.setSubTypeTitle(source.getSubType().getDescription());

        if (source.getAdmittanceHistory() != null) {
            fillAdmitDateFromHistory(source.getAdmittanceHistory(), target);
        } else if (source.getIntakeDate() != null) {
            fillAdmitDateFromIntake(source.getIntakeDate(), target);
        }

        if (source.getEvent() != null) {
            target.setEventId(source.getEvent().getId());
            target.setEventDate(DateTimeUtils.toEpochMilli(source.getEvent().getEventDateTime()));
            target.setEventTypeTitle(source.getEvent().getEventType().getDescription());
        }

        target.setStatusTitle(source.getAuditableStatus().getDisplayName());
        target.setLastModified(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        target.setAuthor(source.getEmployee().getFullName());
        target.setAuthorRoleTitle(source.getEmployee().getCareTeamRole().getName());

        if (source instanceof EncounterNote
                || ObjectUtils.anyNotNull(source.getEncounterFromTime(), source.getEncounterToTime(), source.getClinicianCompletingEncounter(), source.getOtherClinicianCompletingEncounter())) {
            var encounterDto = createEncounter();
            fillEncounter(source, encounterDto);
            target.setEncounter(encounterDto);
        }

        if (source instanceof ServiceStatusCheckNote) {
            var serviceStatusCheckNote = (ServiceStatusCheckNote) source;
            var serviceStatusCheckDto = createServiceStatusCheck();
            fillServiceStatusCheck(serviceStatusCheckNote, serviceStatusCheckDto);
            target.setServiceStatusCheck(serviceStatusCheckDto);
        }

        if (source instanceof ClientProgramNote) {
            var clientProgramNote = (ClientProgramNote) source;
            var clientProgramDto = createClientProgram();
            fillClientProgram(clientProgramNote, clientProgramDto);
            target.setClientProgram(clientProgramDto);
        }

        target.setSubjective(source.getSubjective());
        target.setObjective(source.getObjective());
        target.setPlan(source.getPlan());
        target.setAssessment(source.getAssessment());
    }


    protected abstract void fillGroupNoteClients(Note source, T target);

    protected void fillNonGroupNoteClient(Note source, T target) {
        target.setClientName(source.getClient().getFullName());
    }

    protected void fillAdmitDateFromHistory(AdmittanceHistory admittanceHistory, T target) {
        target.setAdmitDate(DateTimeUtils.toEpochMilli(admittanceHistory.getAdmitDate()));
    }

    protected void fillAdmitDateFromIntake(Instant intakeDate, T target) {
        target.setAdmitDate(DateTimeUtils.toEpochMilli(intakeDate));
    }

    protected abstract E createEncounter();

    protected abstract SSC createServiceStatusCheck();

    protected abstract CP createClientProgram();

    protected void fillEncounter(Note note, E encounterDto) {
        if (note instanceof EncounterNote ) {
            var encounterNote = (EncounterNote) note;
            encounterDto.setTypeTitle(encounterNote.getEncounterNoteType().getDescription());
        }

        if (note.getClinicianCompletingEncounter() != null) {
            encounterDto.setClinicianId(note.getClinicianCompletingEncounter().getId());
            encounterDto.setClinicianTitle(note.getClinicianCompletingEncounter().getFullName());
        }
        encounterDto.setOtherClinician(note.getOtherClinicianCompletingEncounter());
        if (!CareCoordinationUtils.allNull(note.getEncounterFromTime(), note.getEncounterToTime())) {
            encounterDto.setFromDate(DateTimeUtils.toEpochMilli(note.getEncounterFromTime()));
            encounterDto.setToDate(DateTimeUtils.toEpochMilli(note.getEncounterToTime()));
        }
    }

    protected void fillServiceStatusCheck(ServiceStatusCheckNote serviceStatusCheckNote, SSC serviceStatusCheckDto) {
        serviceStatusCheckDto.setServicePlanId(serviceStatusCheckNote.getServicePlan().getId());
        serviceStatusCheckDto.setServicePlanCreatedDate(DateTimeUtils.toEpochMilli(serviceStatusCheckNote.getServicePlan().getDateCreated()));
        serviceStatusCheckDto.setResourceName(serviceStatusCheckNote.getResourceName());
        serviceStatusCheckDto.setProviderName(serviceStatusCheckNote.getProviderName());
        serviceStatusCheckDto.setAuditPerson(serviceStatusCheckNote.getAuditPerson());
        serviceStatusCheckDto.setCheckDate(DateTimeUtils.toEpochMilli(serviceStatusCheckNote.getCheckDate()));
        serviceStatusCheckDto.setNextCheckDate(DateTimeUtils.toEpochMilli(serviceStatusCheckNote.getNextCheckDate()));
        serviceStatusCheckDto.setServiceProvided(serviceStatusCheckNote.getServiceProvided());
        serviceStatusCheckDto.setCanViewServicePlan(servicePlanSecurityService.canView(serviceStatusCheckNote.getServicePlan().getId()));
    }

    protected void fillClientProgram(ClientProgramNote clientProgramNote, CP clientProgramDto) {
        clientProgramDto.setTypeId(clientProgramNote.getClientProgramNoteType().getId());
        clientProgramDto.setTypeTitle(clientProgramNote.getClientProgramNoteType().getDescription());
        clientProgramDto.setServiceProvider(clientProgramNote.getServiceProvider());
        clientProgramDto.setStartDate(DateTimeUtils.toEpochMilli(clientProgramNote.getStartDate()));
        clientProgramDto.setEndDate(DateTimeUtils.toEpochMilli(clientProgramNote.getEndDate()));
    }
}
