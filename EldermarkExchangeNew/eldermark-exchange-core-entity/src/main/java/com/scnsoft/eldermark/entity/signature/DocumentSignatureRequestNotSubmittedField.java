package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureFieldPdcFlowTypeAware;

import javax.persistence.*;

@Entity
@Table(name = "DocumentSignatureRequestNotSubmittedField")
public class DocumentSignatureRequestNotSubmittedField extends BaseDocumentSignatureFieldLocation implements DocumentSignatureFieldPdcFlowTypeAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "signature_request_id", nullable = false)
    private DocumentSignatureRequest signatureRequest;

    @Column(name = "signature_request_id", nullable = false, insertable = false, updatable = false)
    private Long signatureRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pdc_flow_type")
    private TemplateFieldPdcFlowType pdcFlowType;

    @Column(name = "related_field_id", updatable = false, insertable = false)
    private Long relatedFieldId;

    @ManyToOne
    @JoinColumn(name = "related_field_id")
    private DocumentSignatureRequestNotSubmittedField relatedField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public TemplateFieldPdcFlowType getPdcFlowType() {
        return pdcFlowType;
    }

    public void setPdcFlowType(TemplateFieldPdcFlowType pdcFlowType) {
        this.pdcFlowType = pdcFlowType;
    }

    public Long getRelatedFieldId() {
        return relatedFieldId;
    }

    public void setRelatedFieldId(Long relatedFieldId) {
        this.relatedFieldId = relatedFieldId;
    }

    public DocumentSignatureRequestNotSubmittedField getRelatedField() {
        return relatedField;
    }

    public void setRelatedField(DocumentSignatureRequestNotSubmittedField relatedField) {
        this.relatedField = relatedField;
    }
}
