package org.openhealthtools.openxds.registry;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//TODO use org.openhealthtools.openxds.repository.entity.Document
@Table(name = "Document")
@Entity(name = "Document")
public class Document {
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

    @Column(name = "author_db_alt_id", nullable = false)
    private String authorDatabaseAlternativeId;

    @Column(name = "res_legacy_id", nullable = false)
    private String residentLegacyId;

    @Column(name = "author_legacy_id", nullable = false)
    private String authorLegacyId;

    @Column(name = "document_title", nullable = false, columnDefinition = "nvarchar(255)")
    private String documentTitle;

    @Column(name = "original_file_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String originalFileName;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "creation_time", nullable = false)
    private Date creationTime;

    @Column(name = "deletion_time", nullable = true)
    private Date deletionTime;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size")
    private Integer size;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "eldermark_shared", nullable = false)
    private boolean eldermarkShared;

    @Column(name="unique_id")
    private String uniqueId;

    @Column(name = "hash_sum", nullable = false)
    private String hash;

    @ManyToMany
    @JoinTable(name = "Document_SourceDatabase",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "database_id"))

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

    public String getAuthorDatabaseAlternativeId() {
        return authorDatabaseAlternativeId;
    }

    public void setAuthorDatabaseAlternativeId(String authorDatabaseAlternativeId) {
        this.authorDatabaseAlternativeId = authorDatabaseAlternativeId;
    }

    public String getAuthorLegacyId() {
        return authorLegacyId;
    }

    public void setAuthorLegacyId(String authorLegacyId) {
        this.authorLegacyId = authorLegacyId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String fileName) {
        this.originalFileName = fileName;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    public boolean isEldermarkShared() {
        return eldermarkShared;
    }

    public void setEldermarkShared(boolean eldermarkShared) {
        this.eldermarkShared = eldermarkShared;
    }


    public Date getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Date deletionTime) {
        this.deletionTime = deletionTime;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
