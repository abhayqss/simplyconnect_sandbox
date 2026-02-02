package com.scnsoft.eldermark.entity.document.community;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "CommunityDocument")
public class CommunityDocument implements CommunityDocumentFieldsAware, CommunityDocumentSecurityFieldsAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne
    private Employee author;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "author_db_alt_id", nullable = false)
    private String authorOrganizationAlternativeId;

    @Column(name = "author_legacy_id", nullable = false)
    private String authorLegacyId;

    @Column(name = "document_title", nullable = false, columnDefinition = "nvarchar(255)")
    @Nationalized
    private String documentTitle;

    @Column(name = "original_file_name", nullable = false, columnDefinition = "nvarchar(255)")
    @Nationalized
    private String originalFileName;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "creation_time", nullable = false)
    private Instant creationTime;

    @Column(name = "temporary_deleted", columnDefinition = "bit")
    private Boolean temporaryDeleted;

    @JoinColumn(name = "temporary_deleted_by_id", referencedColumnName = "id")
    @ManyToOne
    private Employee temporaryDeletedBy;

    @Column(name = "temporary_deletion_time")
    private Instant temporaryDeletionTime;

    @Column(name = "deletion_time", nullable = true)
    private Instant deletionTime;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "organization_id", nullable = false)
    private Long communityId;

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "update_time")
    private Instant lastModifiedTime;

    @Column(name = "description", length = 5000)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "Document_DocumentCategoryView",
        joinColumns = @JoinColumn(name = "document_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<DocumentCategory> categories;


    public Instant getLastModifiedTime() {
        return lastModifiedTime;
    }


    public void setLastModifiedTime(Instant lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Boolean getTemporaryDeleted() {
        return temporaryDeleted;
    }

    public void setTemporaryDeleted(Boolean temporaryDeleted) {
        this.temporaryDeleted = temporaryDeleted;
    }

    public Employee getTemporaryDeletedBy() {
        return temporaryDeletedBy;
    }

    public void setTemporaryDeletedBy(Employee temporaryDeletedBy) {
        this.temporaryDeletedBy = temporaryDeletedBy;
    }

    public Instant getTemporaryDeletionTime() {
        return temporaryDeletionTime;
    }

    public void setTemporaryDeletionTime(Instant temporaryDeletionTime) {
        this.temporaryDeletionTime = temporaryDeletionTime;
    }

    public Instant getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Instant deletionTime) {
        this.deletionTime = deletionTime;
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

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Override
    public String getAuthorOrganizationAlternativeId() {
        return authorOrganizationAlternativeId;
    }

    @Override
    public String getAuthorLegacyId() {
        return authorLegacyId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public List<DocumentCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategory> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
