package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.notes.RelatedNoteListItemDto;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

@Component
public class RelatedNoteDtoConverter implements ListAndItemConverter<Note, RelatedNoteListItemDto> {

    @Override
    public RelatedNoteListItemDto convert(Note source) {
        RelatedNoteListItemDto target = new RelatedNoteListItemDto();
        target.setId(source.getId());
        target.setAuthor(source.getEmployee().getFullName());
        target.setAuthorRoleTitle(source.getEmployee().getCareTeamRole().getName());
        target.setDate(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        target.setStatusName(source.getAuditableStatus().name());
        target.setStatusTitle(source.getAuditableStatus().getDisplayName());
        target.setSubTypeTitle(source.getSubType().getDescription());
        return target;
    }

}