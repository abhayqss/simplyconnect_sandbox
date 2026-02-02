package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.entity.note.ClientProgramNote;
import com.scnsoft.eldermark.service.ClientProgramNoteTypeService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientProgramDtoToEntityConverter extends BaseNoteDtoToEntityConverter<ClientProgramNote> {

    @Autowired
    private ClientProgramNoteTypeService clientProgramNoteTypeService;

    @Override
    public ClientProgramNote convert(NoteDto source) {
        var target = new ClientProgramNote();
        convertBase(source, target);
        var clientProgram = source.getClientProgram();
        target.setClientProgramNoteType(clientProgramNoteTypeService.findById(clientProgram.getTypeId()));
        target.setServiceProvider(clientProgram.getServiceProvider());
        target.setStartDate(DateTimeUtils.toInstant(clientProgram.getStartDate()));
        target.setEndDate(DateTimeUtils.toInstant(clientProgram.getEndDate()));
        return target;
    }
}
