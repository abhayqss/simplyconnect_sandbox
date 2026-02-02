package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto;

import java.util.List;

public interface NoteSubTypeFacade {

    List<NoteSubTypeDto> getAllPhrVisibleSubTypes();
}
