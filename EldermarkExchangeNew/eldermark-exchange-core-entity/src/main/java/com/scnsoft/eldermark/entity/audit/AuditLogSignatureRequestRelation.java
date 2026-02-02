package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;

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

@Entity
@Table(name = "AuditLogRelation_Signature")
public class AuditLogSignatureRequestRelation extends AuditLogRelation<Long> {

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "AuditLogRelation_SignatureRequest",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "signature_request_id", nullable = false))
    private List<DocumentSignatureRequest> signatureRequests;

    @ElementCollection
    @CollectionTable(name = "AuditLogRelation_SignatureRequest", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "signature_request_id", insertable = false, updatable = false, nullable = false)
    private List<Long> signatureRequestIds;

    public List<DocumentSignatureRequest> getSignatureRequests() {
        return signatureRequests;
    }

    public void setSignatureRequests(List<DocumentSignatureRequest> signatureRequests) {
        this.signatureRequests = signatureRequests;
    }

    public List<Long> getSignatureRequestIds() {
        return signatureRequestIds;
    }

    public void setSignatureRequestIds(List<Long> signatureRequestIds) {
        this.signatureRequestIds = signatureRequestIds;
    }

    @Override
    public List<Long> getRelatedIds() {
        return signatureRequestIds;
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SIGNATURE_REQUEST;
    }
}