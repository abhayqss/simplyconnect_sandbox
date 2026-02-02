package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@MappedSuperclass
public class ExchangeDocumentAwareBasicEntity extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(name = "document_id", insertable = false, updatable = false)
    private Long documentId;

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
}
