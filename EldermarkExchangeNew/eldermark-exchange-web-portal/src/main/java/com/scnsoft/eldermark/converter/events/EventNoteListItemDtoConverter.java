package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import com.scnsoft.eldermark.entity.EventNote;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.client.ClientName;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EventNoteListItemDtoConverter implements ListAndItemConverter<EventNote, EventOrNoteListItemDto> {

    private static final String EVENT_TYPE_ENTITY = "EVENT";
    private static final String NOTE_TYPE_ENTITY = "NOTE";

    @Autowired
    private ClientService clientService;

    @Override
    public EventOrNoteListItemDto convert(EventNote source) {
        EventOrNoteListItemDto target = new EventOrNoteListItemDto();
        target.setId(source.getNumericId());

        target.setDate(DateTimeUtils.toEpochMilli(source.getDate()));
        target.setSubTypeTitle(source.getSubTypeTitle());
        target.setSubTypeName(source.getSubTypeName());
        if (source.getEventId() != null) {
            target.setEntity(EVENT_TYPE_ENTITY);

            target.setTypeName(source.getTypeName());
            target.setTypeTitle(source.getTypeTitle());
            target.setClientName(source.getFullName());

        } else {
            target.setEntity(NOTE_TYPE_ENTITY);

            var noteType = NoteType.valueOf(source.getTypeName());
            target.setTypeName(noteType.name());
            target.setTypeTitle(noteType.getDisplayName());

            if (NoteType.GROUP_NOTE.name().equals(source.getTypeName())) {
                //load names projection separately for better performance
                var clients = clientService.findNoteClientNames(source.getNoteId());
                
                target.setClientName(
                        clients.stream()
                                .map(IdNamesAware::getFullName)
                                .collect(Collectors.joining(", ")));
            } else {
                target.setClientName(source.getFullName());
            }
        }
        return target;
    }

}
