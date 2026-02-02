package com.scnsoft.eldermark.entity.signature;

import javax.persistence.*;

@Entity
@Table(name = "DocumentSignatureRequestSubmittedFieldStyle")
public class DocumentSignatureRequestSubmittedFieldStyle extends BaseDocumentSignatureFieldStyle {

    @ManyToOne
    @JoinColumn(name = "submitted_field_id", nullable = false)
    private DocumentSignatureRequestSubmittedField submittedField;

    public DocumentSignatureRequestSubmittedField getSubmittedField() {
        return submittedField;
    }

    public void setSubmittedField(DocumentSignatureRequestSubmittedField submittedField) {
        this.submittedField = submittedField;
    }
}
