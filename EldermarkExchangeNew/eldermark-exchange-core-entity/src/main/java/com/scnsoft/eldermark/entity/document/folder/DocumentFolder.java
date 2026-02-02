package com.scnsoft.eldermark.entity.document.folder;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "DocumentFolder")
public class DocumentFolder implements DocumentFolderParentAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Employee author;

    @Column(name = "creation_time", nullable = false)
    private Instant creationTime;

    @Column(name = "update_time")
    private Instant updateTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long communityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Community community;

    @Column(name = "is_security_enabled")
    private Boolean isSecurityEnabled;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentFolderPermission> permissions;

    @ElementCollection
    @CollectionTable(
        name = "DocumentFolder_DocumentCategory",
        joinColumns = @JoinColumn(name = "folder_id")
    )
    @Column(name = "category_chain_id")
    private List<Long> categoryChainIds;

    @JoinColumn(name = "temporary_deleted_by_id", referencedColumnName = "id")
    @ManyToOne
    private Employee temporaryDeletedBy;

    @Column(name = "temporary_deletion_time")
    private Instant temporaryDeletionTime;

    @JoinColumn(name = "deleted_by_id", referencedColumnName = "id")
    @ManyToOne
    private Employee deletedBy;

    @Column(name = "deletion_time")
    private Instant deletionTime;

    @JoinColumn(name = "restored_by_id", referencedColumnName = "id")
    @ManyToOne
    private Employee restoredBy;

    @Column(name = "restoration_time")
    private Instant restorationTime;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentFolderType type;

    @ManyToMany
    @JoinTable(
            name = "DocumentSignatureTemplate_DocumentFolder",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "signature_template_id")
    )
    private Set<DocumentSignatureTemplate> documentSignatureTemplates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Boolean getSecurityEnabled() {
        return isSecurityEnabled;
    }

    public void setSecurityEnabled(Boolean securityEnabled) {
        isSecurityEnabled = securityEnabled;
    }

    public Boolean getIsSecurityEnabled() {
        return isSecurityEnabled;
    }

    public void setIsSecurityEnabled(Boolean isSecurityEnabled) {
        this.isSecurityEnabled = isSecurityEnabled;
    }

    public List<DocumentFolderPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DocumentFolderPermission> permissions) {
        this.permissions = permissions;
    }

    public List<Long> getCategoryChainIds() {
        return categoryChainIds;
    }

    public void setCategoryChainIds(List<Long> categoryChainIds) {
        this.categoryChainIds = categoryChainIds;
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

    public Employee getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Employee deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Instant getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Instant deletionTime) {
        this.deletionTime = deletionTime;
    }

    public Employee getRestoredBy() {
        return restoredBy;
    }

    public void setRestoredBy(Employee restoredBy) {
        this.restoredBy = restoredBy;
    }

    public Instant getRestorationTime() {
        return restorationTime;
    }

    public void setRestorationTime(Instant restorationTime) {
        this.restorationTime = restorationTime;
    }

    public DocumentFolderType getType() {
        return type;
    }

    public void setType(DocumentFolderType type) {
        this.type = type;
    }

    public Set<DocumentSignatureTemplate> getDocumentSignatureTemplates() {
        return documentSignatureTemplates;
    }

    public void setDocumentSignatureTemplates(Set<DocumentSignatureTemplate> documentSignatureTemplates) {
        this.documentSignatureTemplates = documentSignatureTemplates;
    }
}
