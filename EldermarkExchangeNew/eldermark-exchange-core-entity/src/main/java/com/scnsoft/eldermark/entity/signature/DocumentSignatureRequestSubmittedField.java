package com.scnsoft.eldermark.entity.signature;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DocumentSignatureRequestSubmittedField")
public class DocumentSignatureRequestSubmittedField extends BaseDocumentSignatureField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "signature_request_id", nullable = false)
    private DocumentSignatureRequest signatureRequest;

    @Column(name = "signature_request_id", nullable = false, insertable = false, updatable = false)
    private Long signatureRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    private SignatureSubmittedFieldType fieldType;

    @Column(name = "pdcflow_overlay_type", columnDefinition = "tinyint")
    private Short pdcflowOverlayType;

    @OneToMany(mappedBy = "submittedField", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentSignatureRequestSubmittedFieldStyle> styles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SignatureSubmittedFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(SignatureSubmittedFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Short getPdcflowOverlayType() {
        return pdcflowOverlayType;
    }

    public void setPdcflowOverlayType(Short pdcflowOverlayType) {
        this.pdcflowOverlayType = pdcflowOverlayType;
    }

    public List<DocumentSignatureRequestSubmittedFieldStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<DocumentSignatureRequestSubmittedFieldStyle> styles) {
        this.styles = styles;
    }
}
