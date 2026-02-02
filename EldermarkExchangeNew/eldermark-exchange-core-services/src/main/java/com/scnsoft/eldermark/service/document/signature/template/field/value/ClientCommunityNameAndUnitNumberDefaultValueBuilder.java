package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClientCommunityNameAndUnitNumberDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_COMMUNITY_AND_UNIT_NUMBER;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(client ->
                        Stream.of(client.getCommunity().getName(), client.getUnitNumber())
                                .filter(StringUtils::isNotEmpty)
                                .collect(Collectors.joining(", "))
                )
                .orElse(null);
    }
}
