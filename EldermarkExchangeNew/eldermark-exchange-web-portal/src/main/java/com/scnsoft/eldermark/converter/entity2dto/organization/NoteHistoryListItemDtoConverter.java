package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.notes.NoteHistoryListItemDto;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

@Component
public class NoteHistoryListItemDtoConverter implements ListAndItemConverter<Note, NoteHistoryListItemDto> {

    @Override
    public NoteHistoryListItemDto convert(Note source) {
        NoteHistoryListItemDto target = new NoteHistoryListItemDto();
        target.setId(source.getId());
        target.setAuthor(source.getEmployee().getFullName());
        target.setModifiedDate(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        target.setAuthorRoleTitle(source.getEmployee().getCareTeamRole().getName());
        target.setStatusName(source.getAuditableStatus().name());
        target.setStatusTitle(source.getAuditableStatus().getDisplayName());
        target.setArchived(source.getArchived());
        return target;
    }

}