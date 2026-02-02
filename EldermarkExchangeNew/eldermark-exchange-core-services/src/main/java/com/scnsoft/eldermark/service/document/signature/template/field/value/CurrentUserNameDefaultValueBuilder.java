package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CurrentUserNameDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_USER_NAME;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getCurrentEmployee())
                .map(Employee::getFullName)
                .orElse(null);
    }
}
