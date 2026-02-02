package com.scnsoft.eldermark.entity.signature;

import javax.persistence.*;

@Entity
@Table(name = "DocumentSignatureTemplateFieldLocation")
public class DocumentSignatureTemplateFieldLocation extends BaseDocumentSignatureField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "signature_template_field_id", nullable = false)
    private DocumentSignatureTemplateField field;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentSignatureTemplateField getField() {
        return field;
    }

    public void setField(DocumentSignatureTemplateField field) {
        this.field = field;
    }
}
