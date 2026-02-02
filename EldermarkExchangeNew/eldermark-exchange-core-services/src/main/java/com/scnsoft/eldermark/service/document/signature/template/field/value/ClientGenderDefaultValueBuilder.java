package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientGenderDefaultValueBuilder implements FieldDefaultValueBuilder {
    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_GENDER;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(Client::getGender)
                .map(CcdCode::getDisplayName)
                .orElse(null);
    }
}
