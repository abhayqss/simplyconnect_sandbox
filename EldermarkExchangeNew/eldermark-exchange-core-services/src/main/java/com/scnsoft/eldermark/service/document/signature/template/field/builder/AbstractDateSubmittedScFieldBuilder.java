package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;

import java.time.Instant;
import java.util.stream.Stream;

abstract class AbstractDateSubmittedScFieldBuilder extends AbstractSubmittedScFieldBuilder {

    protected AbstractDateSubmittedScFieldBuilder(ScSourceTemplateFieldType supportedType) {
        super(supportedType);
    }

    @Override
    public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return Stream.ofNullable(context.getFieldValues().get(field.getName()))
                .map(value -> ((Number) value).longValue())
                .map(Instant::ofEpochMilli)
                .map(date -> extractValue(date, context.getTimezoneOffset()))
                .map(value -> {
                    var submittedField = new DocumentSignatureRequestSubmittedField();
                    submittedField.setValue(value);
                    submittedField.setFieldType(SignatureSubmittedFieldType.TEXT);
                    populateFieldLocation(submittedField, field.getLocations().get(0));
                    return submittedField;
                });
    }

    abstract protected String extractValue(Instant date, Integer timezoneOffset);
}
