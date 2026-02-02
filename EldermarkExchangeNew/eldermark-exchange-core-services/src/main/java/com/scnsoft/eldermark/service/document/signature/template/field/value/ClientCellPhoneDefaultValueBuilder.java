package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientCellPhoneDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_CELL_PHONE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(Client::getPerson)
                .flatMap(person -> PersonTelecomUtils.findValue(person, PersonTelecomCode.MC))
                .orElse(null);
    }
}
