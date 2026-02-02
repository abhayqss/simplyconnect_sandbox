package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.document.Document;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "AuditLogRelation_Document")
public class AuditLogCompanyDocumentRelation extends AuditLogRelation<Long> {

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "AuditLogRelation_CompanyDocument",
            joinColumns = {
                    @JoinColumn(name = "id"),
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "document_id", referencedColumnName = "id", nullable = false),
                    @JoinColumn(name = "document_title", referencedColumnName = "document_title", columnDefinition = "nvarchar(255)")
            })
    private List<Document> documents;

    @ElementCollection
    @CollectionTable(name = "AuditLogRelation_CompanyDocument",
            joinColumns = {
                    @JoinColumn(name = "id")
            })
    @Column(name = "document_id", insertable = false, updatable = false, nullable = false)
    private List<Long> documentIds;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }

    @Override
    public List<Long> getRelatedIds() {
        return documentIds;
    }

    @Override
    public List<String> getAdditionalFields() {
        return documents.stream()
                .map(Document::getDocumentTitle)
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMPANY_DOCUMENT;
    }
}
