package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Document")
public class Document implements Serializable, DocumentFieldsAware, DocumentXdsConnectorFieldsAware {

    private static final long serialVersionUID = 3670307074217263875L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * It could refer to a resident and an author (employee) by their primary keys.
     * But in that case reimporting of data would not be possible. Instead, resident
     * and author are referred to by their legacy (original) IDs and a database id
     * (and it's also not a primary key, but an alternative id). Data integrity
     * isn't ensured at the database level, but data can be reimported without
     * losing relationships between documents and residents, documents and authors.
     */
    @Column(name = "res_db_alt_id", nullable = false)
    private String clientOrganizationAlternativeId;

    @Column(name = "author_db_alt_id", nullable = false)
    private String authorOrganizationAlternativeId;

    @Column(name = "res_legacy_id", nullable = false)
    private String clientLegacyId;

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

    @Column(name = "update_time")
    private Instant updateTime;

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

    @OneToOne
    @JoinColumn(name = "marco_document_log_id")
    private MarcoIntegrationDocument marcoIntegrationDocument;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_obx_id")
    private OBXObservationResult labObx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_research_order_id")
    private LabResearchOrder labResearchOrder;

    @Column(name = "consana_map_id")
    private String consanaMapId;

    @Column(name = "description", length = 3950)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "Document_DocumentCategory",
            joinColumns = @JoinColumn(name = "document_id")
    )
    @Column(name = "category_chain_id")
    private Set<Long> categoryChainIds;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private DocumentFolder folder;

    @OneToOne
    @JoinColumn(name = "signature_request_id")
    private DocumentSignatureRequest signatureRequest;

    @Column(name = "signature_request_id", insertable = false, updatable = false)
    private Long signatureRequestId;

    @ElementCollection
    @CollectionTable(name = "DocumentSignatureHistory",
            joinColumns = @JoinColumn(name = "document_id")
    )
    @Column(name = "signature_request_id")
    private Set<Long> historySignatureRequestIds;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientOrganizationAlternativeId() {
        return clientOrganizationAlternativeId;
    }

    public void setClientOrganizationAlternativeId(String clientOrganizationAlternativeId) {
        this.clientOrganizationAlternativeId = clientOrganizationAlternativeId;
    }

    public String getClientLegacyId() {
        return clientLegacyId;
    }

    public void setClientLegacyId(String clientLegacyId) {
        this.clientLegacyId = clientLegacyId;
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

    public List<Organization> getSharedWithOrganizations() {
        return sharedWithOrganizations;
    }

    public List<Long> getSharedWithOrganizationIds() {
        List<Long> sharedWithDatabasesIds = new ArrayList<Long>();
        if (sharedWithOrganizations != null) {
            for (Organization organization : sharedWithOrganizations) {
                sharedWithDatabasesIds.add(organization.getId());
            }
        }
        return sharedWithDatabasesIds;
    }

    public void setSharedWithOrganizations(List<Organization> sharedWithDatabases) {
        this.sharedWithOrganizations = sharedWithDatabases;
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

    public MarcoIntegrationDocument getMarcoIntegrationDocument() {
        return marcoIntegrationDocument;
    }

    public void setMarcoIntegrationDocument(MarcoIntegrationDocument marcoIntegrationDocument) {
        this.marcoIntegrationDocument = marcoIntegrationDocument;
    }

    public OBXObservationResult getLabObx() {
        return labObx;
    }

    public void setLabObx(OBXObservationResult labObx) {
        this.labObx = labObx;
    }

    public LabResearchOrder getLabResearchOrder() {
        return labResearchOrder;
    }

    public void setLabResearchOrder(LabResearchOrder labResearchOrder) {
        this.labResearchOrder = labResearchOrder;
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

    public Set<Long> getCategoryChainIds() {
        return categoryChainIds;
    }

    public void setCategoryChainIds(Set<Long> categoryChainIds) {
        this.categoryChainIds = categoryChainIds;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public DocumentFolder getFolder() {
        return folder;
    }

    public void setFolder(DocumentFolder folder) {
        this.folder = folder;
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

    public Set<Long> getHistorySignatureRequestIds() {
        return historySignatureRequestIds;
    }

    public void setHistorySignatureRequestIds(Set<Long> historySignatureRequestIds) {
        this.historySignatureRequestIds = historySignatureRequestIds;
    }
}
