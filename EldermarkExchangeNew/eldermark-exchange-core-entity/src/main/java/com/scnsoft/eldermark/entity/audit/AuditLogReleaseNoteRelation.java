package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.ReleaseNote;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_ReleaseNote")
public class AuditLogReleaseNoteRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "release_note_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ReleaseNote releaseNote;

    @Column(name = "release_note_id", nullable = false)
    private Long releaseNoteId;

    @Column(name = "release_note_title")
    private String releaseNoteTitle;

    public ReleaseNote getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(ReleaseNote releaseNote) {
        this.releaseNote = releaseNote;
    }

    public Long getReleaseNoteId() {
        return releaseNoteId;
    }

    public void setReleaseNoteId(Long releaseNoteId) {
        this.releaseNoteId = releaseNoteId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(releaseNoteId);
    }

    public String getReleaseNoteTitle() {
        return releaseNoteTitle;
    }

    public void setReleaseNoteTitle(String releaseNoteTitle) {
        this.releaseNoteTitle = releaseNoteTitle;
    }

    @Override
    public List<String> getAdditionalFields() {
        return List.of(releaseNoteTitle);
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.RELEASE_NOTE;
    }
}
