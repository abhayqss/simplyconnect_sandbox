package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
class CommunityAddressSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    @Autowired
    private Converter<Address, String> displayAddressConverter;

    public CommunityAddressSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_ADDRESS);
    }

    @Override
    protected String extractValue(Community community) {
        return CollectionUtils.isNotEmpty(community.getAddresses())
                ? displayAddressConverter.convert(community.getAddresses().get(0))
                : null;
    }
}
