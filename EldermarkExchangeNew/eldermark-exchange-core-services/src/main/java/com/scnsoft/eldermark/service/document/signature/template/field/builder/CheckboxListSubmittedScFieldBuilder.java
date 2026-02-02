package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

@Component
class CheckboxListSubmittedScFieldBuilder extends AbstractSubmittedScFieldBuilder {

    public CheckboxListSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.CHECKBOX_LIST);
    }

    @Override
    public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return Stream.ofNullable(context.getFieldValues().get(field.getName()))
                .flatMap(value -> value instanceof Collection
                        ? ((Collection<?>) value).stream()
                        : Stream.of(value)
                )
                .map(String::valueOf)
                .flatMap(value -> field.getLocations().stream()
                        .filter(location -> StringUtils.equals(location.getValue(), value))
                        .map(location -> {
                            var submittedField = new DocumentSignatureRequestSubmittedField();
                            submittedField.setValue("true");
                            submittedField.setFieldType(SignatureSubmittedFieldType.CHECKBOX);
                            populateFieldLocation(submittedField, location);
                            return submittedField;
                        })
                );
    }
}
