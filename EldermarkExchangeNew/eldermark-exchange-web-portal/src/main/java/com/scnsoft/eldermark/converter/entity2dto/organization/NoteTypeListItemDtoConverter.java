package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.notes.NoteTypeDto;
import com.scnsoft.eldermark.entity.note.NoteSubType;

@Component
public class NoteTypeListItemDtoConverter implements ListAndItemConverter<NoteSubType, NoteTypeDto> {

    @Override
    public NoteTypeDto convert(NoteSubType source) {
        NoteTypeDto target = new NoteTypeDto();
        target.setId(source.getId());
        target.setTitle(source.getDescription());
        target.setName(source.getCode());
        target.setCanCreate(source.getManual());
        target.setFollowUpCode(source.getFollowUpCode() != null ? source.getFollowUpCode().getCode() : null);
        target.setEncounterCode(source.getEncounterCode() != null ? source.getEncounterCode().getCode() : null);
        target.setCanCreateGroupNote(source.isAllowedForGroupNote());
        target.setCanCreateEventNote(source.getAllowedForEventNote());
        return target;
    }

}
