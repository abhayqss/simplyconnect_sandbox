package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;

public interface NoteDetailsService {

    NoteDto getNoteDetails(Long noteId, boolean includeHistory, int timeZoneOffset);

}
