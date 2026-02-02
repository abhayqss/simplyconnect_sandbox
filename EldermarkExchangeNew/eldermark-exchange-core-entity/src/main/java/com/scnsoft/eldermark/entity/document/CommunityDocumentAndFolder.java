package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "CommunityDocumentAndFolderView")
public class CommunityDocumentAndFolder implements DocumentFileFieldsAware {

    @Id
    private String id;

    @Column(name = "title", nullable = false, columnDefinition = "nvarchar(255)")
    @Nationalized
    private String title;

    @Column(name = "organization_id", nullable = false, updatable = false, insertable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Community community;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "description")
    private String description;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAndFolderType type;

    @Column(name = "last_modified_time")
    private Instant lastModifiedTime;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @Column(name = "author_db_alt_id")
    private String authorOrganizationAlternativeId;

    @Column(name = "author_legacy_id")
    private String authorLegacyId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "size")
    private Integer size;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "is_security_enabled")
    private Boolean isSecurityEnabled;

    @Column(name = "temporary_deletion_time")
    private Instant temporaryDeletionTime;

    @Column(name = "deletion_time")
    private Instant deletionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private DocumentSignatureTemplate template;

    @Column(name = "template_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentSignatureTemplateStatus templateStatus;

    @OneToMany
    @JoinTable(
        name = "CommunityDocumentAndFolder_CategoryView",
        joinColumns = @JoinColumn(name = "id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<DocumentCategory> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
        setCommunityId(community.getId());
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentAndFolderType getType() {
        return type;
    }

    public void setType(DocumentAndFolderType type) {
        this.type = type;
    }

    public Instant getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Instant updateDate) {
        this.lastModifiedTime = updateDate;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    @Override
    public String getAuthorOrganizationAlternativeId() {
        return authorOrganizationAlternativeId;
    }

    public void setAuthorOrganizationAlternativeId(String authorOrganizationAlternativeId) {
        this.authorOrganizationAlternativeId = authorOrganizationAlternativeId;
    }

    @Override
    public String getAuthorLegacyId() {
        return authorLegacyId;
    }

    public void setAuthorLegacyId(String authorLegacyId) {
        this.authorLegacyId = authorLegacyId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Boolean getSecurityEnabled() {
        return isSecurityEnabled;
    }

    public void setSecurityEnabled(Boolean securityEnabled) {
        isSecurityEnabled = securityEnabled;
    }

    public List<DocumentCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategory> categories) {
        this.categories = categories;
    }

    public Instant getTemporaryDeletionTime() {
        return temporaryDeletionTime;
    }

    public void setTemporaryDeletionTime(Instant temporaryDeletedTime) {
        this.temporaryDeletionTime = temporaryDeletedTime;
    }

    public Instant getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Instant deletionTime) {
        this.deletionTime = deletionTime;
    }

    public DocumentSignatureTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DocumentSignatureTemplate template) {
        this.template = template;
    }

    public DocumentSignatureTemplateStatus getTemplateStatus() {
        return templateStatus;
    }

    public void setTemplateStatus(DocumentSignatureTemplateStatus status) {
        this.templateStatus = status;
    }
}
