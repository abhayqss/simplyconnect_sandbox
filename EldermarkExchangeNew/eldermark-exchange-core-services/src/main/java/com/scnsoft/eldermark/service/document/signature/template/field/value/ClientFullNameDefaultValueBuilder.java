package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientFullNameDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_FULL_NAME;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(client ->
                        CareCoordinationUtils.getFullName(
                                client.getFirstName(),
                                client.getMiddleName(),
                                client.getLastName()
                        )
                )
                .orElse(null);
    }
}
