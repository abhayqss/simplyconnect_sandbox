package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.document.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_ClientDocument")
public class AuditLogClientDocumentRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "document_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Document document;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_title", columnDefinition = "nvarchar(255)")
    private String documentTitle;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(documentId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return List.of(documentTitle);
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.DOCUMENT;
    }
}
