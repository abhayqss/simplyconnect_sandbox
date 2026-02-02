package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.note.NoteViewDataConverter;
import com.scnsoft.eldermark.dto.notes.ClientProgramDto;
import com.scnsoft.eldermark.dto.notes.EncounterDto;
import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.dto.notes.ServiceStatusCheckDto;
import com.scnsoft.eldermark.entity.client.ClientNameAndStatusAware;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.EncounterNoteType;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.NoteSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.ViewableIdentifiedActiveAwareNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class NoteDtoConverter extends NoteViewDataConverter<EncounterDto, ServiceStatusCheckDto, ClientProgramDto, NoteDto> implements Converter<Note, NoteDto> {

    @Autowired
    private NoteSecurityService noteSecurityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Override
    protected void fill(Note source, NoteDto target) {
        super.fill(source, target);

        target.setId(source.getId());

        target.setTypeName(source.getType().name());
        target.setSubTypeId(source.getSubType().getId());
        target.setStatusName(source.getAuditableStatus().name());

        target.setNoteDate(DateTimeUtils.toEpochMilli(source.getNoteDate()));

        target.setCanEdit(noteSecurityService.canEdit(source.getId()));
    }

    @Override
    protected void fillGroupNoteClients(Note source, NoteDto target) {
        //load names projection separately for better performance
        var clients = clientService.findNoteClientNames(source.getId());

        target.setClients(clients.stream()
                .map(this::convertClient)
                .collect(Collectors.toList())
        );
    }

    private ViewableIdentifiedActiveAwareNamedEntityDto convertClient(ClientNameAndStatusAware client) {
        return new ViewableIdentifiedActiveAwareNamedEntityDto(client.getId(), client.getFullName(), clientSecurityService.canView(client.getId()), client.getActive());
    }

    @Override
    protected NoteDto create() {
        return new NoteDto();
    }

    @Override
    protected void fillNonGroupNoteClient(Note source, NoteDto target) {
        super.fillNonGroupNoteClient(source, target);
        target.setClientId(source.getClient().getId());
        target.setCanViewClient(clientSecurityService.canView(source.getClient().getId()));
        target.setClientActive(source.getClient().getActive());
    }

    @Override
    protected void fillAdmitDateFromHistory(AdmittanceHistory admittanceHistory, NoteDto target) {
        super.fillAdmitDateFromHistory(admittanceHistory, target);
        target.setAdmitDateId(admittanceHistory.getId());
    }

    @Override
    protected void fillAdmitDateFromIntake(Instant intakeDate, NoteDto target) {
        super.fillAdmitDateFromIntake(intakeDate, target);
        target.setAdmitDateId(CareCoordinationConstants.ADMIT_DATE_FROM_INTAKE_DATE_ID);
    }

    @Override
    protected EncounterDto createEncounter() {
        return new EncounterDto();
    }

    @Override
    protected void fillEncounter(Note note, EncounterDto encounterDto) {
        super.fillEncounter(note, encounterDto);
        if (note instanceof EncounterNote ) {
            var encounterNote = (EncounterNote) note;
            encounterDto.setTypeId(encounterNote.getEncounterNoteType().getId());
        }
    }

    @Override
    protected ServiceStatusCheckDto createServiceStatusCheck() {
        return new ServiceStatusCheckDto();
    }

    @Override
    protected ClientProgramDto createClientProgram() {
        return new ClientProgramDto();
    }
}
