package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Note;

public interface NoteNotificationService {

    void sendNoteNotifications(Note note);

}
