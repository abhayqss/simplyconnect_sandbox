package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientHomeOrCellPhoneDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Autowired
    private ClientHomePhoneDefaultValueBuilder homePhoneBuilder;

    @Autowired
    private ClientCellPhoneDefaultValueBuilder cellPhoneBuilder;

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_HOME_OR_CELL_PHONE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(homePhoneBuilder.build(context))
                .orElse(cellPhoneBuilder.build(context));
    }
}
