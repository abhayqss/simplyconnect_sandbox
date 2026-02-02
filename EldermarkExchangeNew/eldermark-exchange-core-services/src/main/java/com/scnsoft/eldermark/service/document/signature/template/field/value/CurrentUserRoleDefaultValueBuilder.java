package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserRoleDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_USER_ROLE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getCurrentEmployee())
                .map(Employee::getCareTeamRole)
                .map(CareTeamRole::getName)
                .orElse(null);
    }
}
