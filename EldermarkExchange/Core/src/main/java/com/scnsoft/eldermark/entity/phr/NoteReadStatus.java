package com.scnsoft.eldermark.entity.phr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "Note_ReadStatus",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "note_id"})})
public class NoteReadStatus extends BaseEntity {

    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "user_id")
    private Long userId;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

