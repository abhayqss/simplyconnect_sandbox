package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureBulkRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_SignatureBulkRequest")
public class AuditLogSignatureBulkRequestRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "signature_bulk_request_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DocumentSignatureBulkRequest signatureBulkRequest;

    @Column(name = "signature_bulk_request_id", nullable = false)
    private Long signatureBulkRequestId;

    public DocumentSignatureBulkRequest getSignatureBulkRequest() {
        return signatureBulkRequest;
    }

    public void setSignatureBulkRequest(DocumentSignatureBulkRequest signatureBulkRequest) {
        this.signatureBulkRequest = signatureBulkRequest;
    }

    public Long getSignatureBulkRequestId() {
        return signatureBulkRequestId;
    }

    public void setSignatureBulkRequestId(Long signatureBulkRequestId) {
        this.signatureBulkRequestId = signatureBulkRequestId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(signatureBulkRequestId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SIGNATURE_BULK_REQUEST;
    }
}
