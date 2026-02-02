package com.scnsoft.eldermark.entity.signature;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DocumentSignatureTemplateFieldStyle")
public class DocumentSignatureTemplateFieldStyle extends BaseDocumentSignatureFieldStyle {

    @ManyToOne
    @JoinColumn(name = "signature_template_field_id", nullable = false)
    private DocumentSignatureTemplateField templateField;

    public DocumentSignatureTemplateField getTemplateField() {
        return templateField;
    }

    public void setTemplateField(final DocumentSignatureTemplateField templateField) {
        this.templateField = templateField;
    }
}
