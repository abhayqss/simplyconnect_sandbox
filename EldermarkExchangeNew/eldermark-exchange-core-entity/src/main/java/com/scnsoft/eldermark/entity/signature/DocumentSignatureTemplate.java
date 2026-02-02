package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateFileAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "DocumentSignatureTemplate")
public class DocumentSignatureTemplate implements Serializable, IdAware, DocumentSignatureTemplateFileAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "form_ui_schema")
    private String formUiSchema;

    @Column(name = "form_schema")
    private String formSchema;

    @Column(name = "is_manually_created")
    private Boolean isManuallyCreated;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
        name = "DocumentSignatureTemplate_Organization",
        joinColumns = @JoinColumn(name = "signature_template_id"),
        inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<Community> communities;

    @ElementCollection
    @CollectionTable(
            name = "DocumentSignatureTemplate_Organization",
            joinColumns = @JoinColumn(name = "signature_template_id", nullable = false)
    )
    @Column(name = "organization_id", nullable = false, insertable = false, updatable = false)
    private Set<Long> communityIds;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
        name = "DocumentSignatureTemplate_SourceDatabase",
        joinColumns = @JoinColumn(name = "signature_template_id"),
        inverseJoinColumns = @JoinColumn(name = "database_id")
    )
    private List<Organization> organizations;

    @ElementCollection
    @CollectionTable(
            name = "DocumentSignatureTemplate_SourceDatabase",
            joinColumns = @JoinColumn(name = "signature_template_id", nullable = false)
    )
    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Set<Long> organizationIds;

    @ElementCollection
    @CollectionTable(
            name = "DocumentSignatureTemplate_DocumentFolder",
            joinColumns = @JoinColumn(name = "signature_template_id", nullable = false)
    )
    @Column(name = "folder_id", nullable = false, insertable = false, updatable = false)
    private Set<Long> folderIds;

    @OneToMany(mappedBy = "signatureTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentSignatureTemplateField> fields;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Employee createdBy;

    @Column(name = "creation_datetime")
    private Instant creationDatetime;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Employee updatedBy;

    @Column(name = "update_datetime")
    private Instant updateDatetime;

    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private Employee deletedBy;

    @Column(name = "delete_datetime")
    private Instant deleteDatetime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentSignatureTemplateStatus status;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormUiSchema() {
        return formUiSchema;
    }

    public void setFormUiSchema(String formUiSchema) {
        this.formUiSchema = formUiSchema;
    }

    public String getFormSchema() {
        return formSchema;
    }

    public void setFormSchema(String formSchema) {
        this.formSchema = formSchema;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<DocumentSignatureTemplateField> getFields() {
        return fields;
    }

    public void setFields(List<DocumentSignatureTemplateField> fields) {
        this.fields = fields;
    }

    @Override
    public Boolean getIsManuallyCreated() {
        return isManuallyCreated;
    }

    public void setIsManuallyCreated(Boolean isManuallyCreated) {
        this.isManuallyCreated = isManuallyCreated;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Employee getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Employee updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Instant updateDateTime) {
        this.updateDatetime = updateDateTime;
    }

    public Employee getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Employee deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Instant getDeleteDatetime() {
        return deleteDatetime;
    }

    public void setDeleteDatetime(Instant deleteDatetime) {
        this.deleteDatetime = deleteDatetime;
    }

    public DocumentSignatureTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentSignatureTemplateStatus status) {
        this.status = status;
    }

    public Set<Long> getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(Set<Long> folderIds) {
        this.folderIds = folderIds;
    }
}
