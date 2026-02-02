package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.service.EncounterNoteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EncounterNoteDtoToEntityConverter extends BaseNoteDtoToEntityConverter<EncounterNote> {

    @Autowired
    private EncounterNoteTypeService encounterNoteTypeService;

    @Override
    public EncounterNote convert(NoteDto source) {
        EncounterNote target = new EncounterNote();
        convertBase(source, target);
        if (source.getEncounter().getTypeId() != null) {
            target.setEncounterNoteType(encounterNoteTypeService.findById(source.getEncounter().getTypeId()));
        }
        return target;
    }

}
