package com.scnsoft.eldermark.entity.inbound.document;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentErrorType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DocumentAssignmentLog")
public class DocumentAssignmentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "received_timestamp", nullable = false)
    private Date receivedTime;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @OneToOne
    @JoinColumn(name = "input_path_id")
    private DocumentAssignmentInputPath inputPath;

    @Column(name = "organization_name")
    private String organizationName;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(name = "unassigned_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAssignmentErrorType unassignedReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public DocumentAssignmentInputPath getInputPath() {
        return inputPath;
    }

    public void setInputPath(DocumentAssignmentInputPath inputPath) {
        this.inputPath = inputPath;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public DocumentAssignmentErrorType getUnassignedReason() {
        return unassignedReason;
    }

    public void setUnassignedReason(DocumentAssignmentErrorType unassignedReason) {
        this.unassignedReason = unassignedReason;
    }
}