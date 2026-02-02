package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class ClientFirstNameSubmittedScFieldBuilder extends AbstractSubmittedScFieldBuilder {

    public ClientFirstNameSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.CLIENT_FIRST_NAME);
    }

    @Override
    public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return Stream.ofNullable(context.getClient())
                .map(client -> {
                    var submittedField = new DocumentSignatureRequestSubmittedField();
                    submittedField.setValue(client.getFirstName());
                    submittedField.setFieldType(SignatureSubmittedFieldType.TEXT);
                    populateFieldLocation(submittedField, field.getLocations().get(0));
                    return submittedField;
                });
    }
}
