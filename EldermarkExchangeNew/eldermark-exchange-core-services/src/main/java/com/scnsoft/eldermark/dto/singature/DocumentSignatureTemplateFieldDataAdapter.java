package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.signature.*;

import java.util.ArrayList;
import java.util.List;

public class DocumentSignatureTemplateFieldDataAdapter implements DocumentSignatureFieldData {

    private final DocumentSignatureTemplateField field;

    public DocumentSignatureTemplateFieldDataAdapter(DocumentSignatureTemplateField field) {
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
        return field.getScSourceFieldType();
    }

    @Override
    public BaseDocumentSignatureFieldLocation getLocation() {
        return field.getLocations().get(0);
    }

    @Override
    public Long getRelatedFieldId() {
        return field.getRelatedFieldId();
    }

    @Override
    public String getRelatedFieldValue() {
        return field.getRelatedFieldValue();
    }

    @Override
    public List<BaseDocumentSignatureFieldStyle> getStyles() {
        return new ArrayList<>(field.getStyles());
    }
}
