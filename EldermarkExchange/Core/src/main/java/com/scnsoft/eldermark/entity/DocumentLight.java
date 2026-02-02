package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

/**
 * This entity is introduced just to overcome a bug related to {@code @ManyToOne(fetch = FetchType.LAZY)} annotation not working on non-primary key relationships (see {@code private Resident residentLegacy}).
 * Originally it's intended for usage in {@code DocumentDaoImpl} class only.
 */
@SuppressWarnings("unused")
@Immutable
@Table(name = "Document")
@Entity(name = "DocumentLight")
public class DocumentLight {

    @Id
    @GeneratedValue
    private Long id;

    /*
    * It could refer to a resident and an author (employee) by their primary keys.
    * But in that case reimporting of data would not be possible. Instead, resident and author are referred to
    * by their legacy (original) IDs and a database id (and it's also not a primary key, but an alternative id).
    * Data integrity isn't ensured at the database level, but data can be reimported without losing relationships
    * between documents and residents, documents and authors.
    * */
    @Column(name = "res_db_alt_id", nullable = false)
    private String residentDatabaseAlternativeId;

    @Column(name = "res_legacy_id", nullable = false)
    private String residentLegacyId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "res_legacy_id", referencedColumnName = "legacy_id", nullable = false, insertable = false, updatable = false)
    private Resident residentLegacy;

    @Column(name = "document_title", nullable = false, columnDefinition = "nvarchar(255)")
    private String documentTitle;

    @Column(name = "deletion_time", nullable = true)
    private Date deletionTime;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResidentDatabaseAlternativeId() {
        return residentDatabaseAlternativeId;
    }

    public void setResidentDatabaseAlternativeId(String residentDatabaseAlternativeId) {
        this.residentDatabaseAlternativeId = residentDatabaseAlternativeId;
    }

    public String getResidentLegacyId() {
        return residentLegacyId;
    }

    public void setResidentLegacyId(String residentLegacyId) {
        this.residentLegacyId = residentLegacyId;
    }

    public Resident getResidentLegacy() {
        return residentLegacy;
    }

    public void setResidentLegacy(Resident residentLegacy) {
        this.residentLegacy = residentLegacy;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Date getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Date deletionTime) {
        this.deletionTime = deletionTime;
    }

}
