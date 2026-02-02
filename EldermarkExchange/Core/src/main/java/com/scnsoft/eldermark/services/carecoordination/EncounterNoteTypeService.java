package com.scnsoft.eldermark.services.carecoordination;

import java.util.List;

import com.scnsoft.eldermark.entity.EncounterNoteType;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

public interface EncounterNoteTypeService {

    EncounterNoteType getById(Long id);
    
    List<KeyValueDto> getAllEncounterNoteTypes();
}
