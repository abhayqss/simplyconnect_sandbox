package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClientCityAndStateDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_CITY_AND_STATE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(Client::getPerson)
                .map(Person::getAddresses)
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .map(address ->
                        Stream.of(address.getCity(), address.getState())
                                .filter(StringUtils::isNotEmpty)
                                .collect(Collectors.joining("/"))
                )
                .orElse(null);
    }
}
