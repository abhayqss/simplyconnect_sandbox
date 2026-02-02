package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class BaseNoteService<N extends Note> extends BaseAuditableService<N> {

    @Autowired
    private EventNotificationService eventNotificationService;

    @Override
    public Long createAuditableEntity(N entity) {
        validateNoteSubType(entity);
        var result = super.createAuditableEntity(entity);
        send(result);
        return result;
    }

    protected void validateNoteSubType(N entity) {
        if (NoteType.GROUP_NOTE.equals(entity.getType()) && !entity.getSubType().isAllowedForGroupNote()) {
            throw new ValidationException(entity.getSubType().getDescription() + " sub type is not allowed for group note.");
        }
        if (NoteType.EVENT_NOTE.equals(entity.getType()) && !entity.getSubType().getAllowedForEventNote()) {
            throw new ValidationException(entity.getSubType().getDescription() + " sub type is not allowed for event note.");
        }
    }

    @Override
    public Long updateAuditableEntity(N entity) {
        var result = super.updateAuditableEntity(entity);
        send(result);
        return result;
    }

    private void send(Long result) {
        eventNotificationService.send(findById(result));
    }
}
