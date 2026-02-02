package com.scnsoft.eldermark.converter.note;

import com.scnsoft.eldermark.converter.ClientInfoNotificationDtoConverter;
import com.scnsoft.eldermark.dto.notification.note.NoteClientProgramMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.note.NoteEncounterMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteServiceStatusCheckMailDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.AuditableEntityStatus;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class NoteDetailsMailDtoConverter extends NoteViewDataConverter<NoteEncounterMailDto, NoteServiceStatusCheckMailDto, NoteClientProgramMailDto, NoteDetailsNotificationDto> {

    @Autowired
    private ClientInfoNotificationDtoConverter clientInfoNotificationDtoConverter;

    @Override
    protected NoteDetailsNotificationDto create() {
        return new NoteDetailsNotificationDto();
    }

    @Override
    protected void fill(Note source, NoteDetailsNotificationDto target) {
        super.fill(source, target);

        target.setNew(AuditableEntityStatus.CREATED.equals(source.getAuditableStatus()));
        if (!NoteType.GROUP_NOTE.equals(source.getType())) {
            target.setClientInfo(clientInfoNotificationDtoConverter.convert(source.getClient()));
        } else {
            target.setGroupNote(true);
        }
    }

    @Override
    protected void fillGroupNoteClients(Note source, NoteDetailsNotificationDto target) {
        target.setClientName(concatClientNames(source.getNoteClients()));
    }

    protected String concatClientNames(List<Client> clients) {
        return clients.stream()
                .map(Client::getFullName)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));

    }

    @Override
    protected NoteEncounterMailDto createEncounter() {
        return new NoteEncounterMailDto();
    }

    @Override
    protected NoteServiceStatusCheckMailDto createServiceStatusCheck() {
        return new NoteServiceStatusCheckMailDto();
    }

    @Override
    protected NoteClientProgramMailDto createClientProgram() {
        return new NoteClientProgramMailDto();
    }

    @Override
    protected void fillEncounter(Note note, NoteEncounterMailDto encounterDto) {
        super.fillEncounter(note, encounterDto);

        if (ObjectUtils.allNotNull(note.getEncounterFromTime(), note.getEncounterToTime())) {
            long totalSpentTime = Duration.between(note.getEncounterFromTime(), note.getEncounterToTime()).toMinutes();

            var units = totalSpentTime / 15;
            var r = totalSpentTime % 15;

            if (r > 7) {
                units += 1;
            }

            var startRange = Math.max(units * 15 - 7, 0);
            var endRange = units * 15 + 7;

            encounterDto.setUnits(units);
            encounterDto.setTotalTime(totalSpentTime);
            encounterDto.setRange(startRange + " mins - " + endRange + " mins");
        }
    }


}
