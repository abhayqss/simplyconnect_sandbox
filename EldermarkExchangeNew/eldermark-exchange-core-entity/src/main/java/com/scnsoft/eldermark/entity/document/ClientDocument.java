package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.document.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ClientDocument")
public class ClientDocument implements DocumentFieldsAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "resident_id", referencedColumnName = "id")
    @ManyToOne
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne
    private Employee author;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "res_db_alt_id", nullable = false)
    private String clientOrganizationAlternativeId;

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

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "eldermark_shared", nullable = false)
    private boolean eldermarkShared;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "hash_sum", nullable = false)
    private String hash;

    // null value means that check wasn't performed
    @Column(name = "is_cda")
    private Boolean isCDA;

    @ManyToMany
    @JoinTable(name = "Document_SourceDatabase", joinColumns = @JoinColumn(name = "document_id"), inverseJoinColumns = @JoinColumn(name = "database_id"))
    private List<Organization> sharedWithOrganizations;

    @ElementCollection
    @CollectionTable(
            name = "Document_SourceDatabase",
            joinColumns = @JoinColumn(name = "document_id")
    )
    @Column(name = "database_id")
    private List<Long> sharedWithOrganizationIds;

    @OneToOne
    @JoinColumn(name = "marco_document_log_id")
    private MarcoIntegrationDocument marcoIntegrationDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "document_type", insertable = false, updatable = false)
    private String documentTypeStr;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_research_order_id")
    private LabResearchOrder labResearchOrder;

    @Column(name = "lab_research_order_id", insertable = false, updatable = false)
    private Long labResearchOrderId;

    @Column(name = "consana_map_id")
    private String consanaMapId;

    @Column(name = "description", length = 5000)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "Document_DocumentCategoryView",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<DocumentCategory> categories;

    @Column(name = "update_time")
    private Instant updateTime;

    @OneToOne
    @JoinColumn(name = "signature_request_id")
    private DocumentSignatureRequest signatureRequest;

    @Column(name = "signature_request_id", insertable = false, updatable = false)
    private Long signatureRequestId;

    @Column(name = "is_cloud")
    private Boolean isCloud;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public String getClientOrganizationAlternativeId() {
        return clientOrganizationAlternativeId;
    }

    public void setClientOrganizationAlternativeId(String clientOrganizationAlternativeId) {
        this.clientOrganizationAlternativeId = clientOrganizationAlternativeId;
    }

    public String getAuthorOrganizationAlternativeId() {
        return authorOrganizationAlternativeId;
    }

    public void setAuthorOrganizationAlternativeId(String authorOrganizationAlternativeId) {
        this.authorOrganizationAlternativeId = authorOrganizationAlternativeId;
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

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public boolean getEldermarkShared() {
        return eldermarkShared;
    }

    public void setEldermarkShared(boolean eldermarkShared) {
        this.eldermarkShared = eldermarkShared;
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

    public Boolean getIsCDA() {
        return isCDA;
    }

    public void setIsCDA(Boolean CDA) {
        isCDA = CDA;
    }

    public List<Organization> getSharedWithOrganizations() {
        return sharedWithOrganizations;
    }

    public void setSharedWithOrganizations(List<Organization> sharedWithOrganizations) {
        this.sharedWithOrganizations = sharedWithOrganizations;
    }

    public List<Long> getSharedWithOrganizationIds() {
        return sharedWithOrganizationIds;
    }

    public void setSharedWithOrganizationIds(List<Long> sharedWithOrganizationIds) {
        this.sharedWithOrganizationIds = sharedWithOrganizationIds;
    }

    public MarcoIntegrationDocument getMarcoIntegrationDocument() {
        return marcoIntegrationDocument;
    }

    public void setMarcoIntegrationDocument(MarcoIntegrationDocument marcoIntegrationDocument) {
        this.marcoIntegrationDocument = marcoIntegrationDocument;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeStr() {
        return documentTypeStr;
    }

    public void setDocumentTypeStr(String documentTypeStr) {
        this.documentTypeStr = documentTypeStr;
    }

    public LabResearchOrder getLabResearchOrder() {
        return labResearchOrder;
    }

    public void setLabResearchOrder(LabResearchOrder labResearchOrder) {
        this.labResearchOrder = labResearchOrder;
    }

    public Long getLabResearchOrderId() {
        return labResearchOrderId;
    }

    public void setLabResearchOrderId(Long labResearchOrderId) {
        this.labResearchOrderId = labResearchOrderId;
    }

    public String getConsanaMapId() {
        return consanaMapId;
    }

    public void setConsanaMapId(String consanaMapId) {
        this.consanaMapId = consanaMapId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DocumentCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategory> categories) {
        this.categories = categories;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public DocumentSignatureRequest getSignatureRequest() {
        return signatureRequest;
    }

    public void setSignatureRequest(DocumentSignatureRequest signatureRequest) {
        this.signatureRequest = signatureRequest;
    }

    public Long getSignatureRequestId() {
        return signatureRequestId;
    }

    public void setSignatureRequestId(Long signatureRequestId) {
        this.signatureRequestId = signatureRequestId;
    }

    public Boolean getIsCloud() {
        return isCloud;
    }

    public void setIsCloud(Boolean isCloud) {
        this.isCloud = isCloud;
    }
}
