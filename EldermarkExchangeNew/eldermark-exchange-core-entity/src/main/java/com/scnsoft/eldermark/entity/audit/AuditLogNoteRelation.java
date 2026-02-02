package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.note.Note;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Note")
public class AuditLogNoteRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "note_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Note note;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(noteId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.NOTE;
    }
}
