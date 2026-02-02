package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "AuditLogRelation_SignatureTemplate")
public class AuditLogSignatureTemplateRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "signature_template_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DocumentSignatureTemplate signatureTemplate;

    @Column(name = "signature_template_id", nullable = false)
    private Long signatureTemplateId;

    @Column(name = "signature_template_name")
    private String signatureTemplateName;

    public DocumentSignatureTemplate getSignatureTemplate() {
        return signatureTemplate;
    }

    public void setSignatureTemplate(DocumentSignatureTemplate signatureTemplate) {
        this.signatureTemplate = signatureTemplate;
    }

    public Long getSignatureTemplateId() {
        return signatureTemplateId;
    }

    public void setSignatureTemplateId(Long signatureTemplateId) {
        this.signatureTemplateId = signatureTemplateId;
    }

    public String getSignatureTemplateName() {
        return signatureTemplateName;
    }

    public void setSignatureTemplateName(String signatureTemplateName) {
        this.signatureTemplateName = signatureTemplateName;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(signatureTemplateId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Stream.ofNullable(signatureTemplateName)
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SIGNATURE_TEMPLATE;
    }
}
