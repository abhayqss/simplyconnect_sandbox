package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldLocation;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;

import java.util.stream.Stream;

public abstract class AbstractSubmittedScFieldBuilder implements SubmittedScFieldBuilder {

    private final ScSourceTemplateFieldType supportedType;

    protected AbstractSubmittedScFieldBuilder(ScSourceTemplateFieldType supportedType) {
        this.supportedType = supportedType;
    }

    @Override
    abstract public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    );

    protected void populateFieldLocation(
            DocumentSignatureRequestSubmittedField result,
            DocumentSignatureTemplateFieldLocation location
    ) {
        result.setPageNo(location.getPageNo());
        result.setBottomRightX(location.getBottomRightX());
        result.setBottomRightY(location.getBottomRightY());
        result.setTopLeftX(location.getTopLeftX());
        result.setTopLeftY(location.getTopLeftY());
    }

    @Override
    public ScSourceTemplateFieldType getTemplateFieldType() {
        return supportedType;
    }
}
