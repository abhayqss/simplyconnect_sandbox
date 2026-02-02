package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClientCommunityAddressDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Autowired
    private Converter<Address, String> displayAddressConverter;

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_COMMUNITY_ADDRESS;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(Client::getCommunity)
                .map(Community::getAddresses)
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .map(displayAddressConverter::convert)
                .orElse(null);
    }
}
