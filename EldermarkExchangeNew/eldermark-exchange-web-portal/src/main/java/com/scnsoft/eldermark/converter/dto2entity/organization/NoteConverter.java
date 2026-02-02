package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.notes.NoteDto;
import com.scnsoft.eldermark.entity.note.Note;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class NoteConverter extends BaseNoteDtoToEntityConverter<Note> {

    @Override
    public Note convert(NoteDto source) {
        Note target = new Note();
        convertBase(source, target);
        return target;
    }

}
