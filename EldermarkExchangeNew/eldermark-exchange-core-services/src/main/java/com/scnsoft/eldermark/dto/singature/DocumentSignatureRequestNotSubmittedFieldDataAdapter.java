package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.signature.*;

import java.util.List;

public class DocumentSignatureRequestNotSubmittedFieldDataAdapter implements DocumentSignatureFieldData {

    private final DocumentSignatureRequestNotSubmittedField field;

    public DocumentSignatureRequestNotSubmittedFieldDataAdapter(DocumentSignatureRequestNotSubmittedField field) {
        this.field = field;
    }

    @Override
    public TemplateFieldPdcFlowType getPdcFlowType() {
        return field.getPdcFlowType();
    }

    @Override
    public Long getId() {
        return field.getId();
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public ScSourceTemplateFieldType getScSourceFieldType() {
        return null;
    }

    @Override
    public BaseDocumentSignatureFieldLocation getLocation() {
        return field;
    }

    @Override
    public Long getRelatedFieldId() {
        return field.getRelatedFieldId();
    }

    @Override
    public String getRelatedFieldValue() {
        return null;
    }

    @Override
    public List<BaseDocumentSignatureFieldStyle> getStyles() {
        return List.of();
    }
}
