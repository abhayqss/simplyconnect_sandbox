package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClientStateDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_STATE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(Client::getPerson)
                .map(Person::getAddresses)
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .map(PersonAddress::getState)
                .orElse(null);
    }
}
