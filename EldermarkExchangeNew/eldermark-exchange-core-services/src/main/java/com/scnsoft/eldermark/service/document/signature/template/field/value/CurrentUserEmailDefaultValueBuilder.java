package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserEmailDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_USER_EMAIL;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getCurrentEmployee())
                .flatMap(employee -> PersonTelecomUtils.find(employee.getPerson(), PersonTelecomCode.EMAIL))
                .map(PersonTelecom::getNormalized)
                .orElse(null);
    }
}
