package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.note.NoteSubType.EncounterCode;
import com.scnsoft.eldermark.entity.note.NoteSubType.FollowUpCode;

public interface NoteSubTypeService {
    NoteSubType findById(Long subTypeId);
    NoteSubType findByCode(String code);
}
