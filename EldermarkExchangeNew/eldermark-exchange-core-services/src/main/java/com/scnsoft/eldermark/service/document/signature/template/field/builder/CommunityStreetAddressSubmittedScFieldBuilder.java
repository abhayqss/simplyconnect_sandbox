package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
class CommunityStreetAddressSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityStreetAddressSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_STREET_ADDRESS);
    }

    @Override
    protected String extractValue(Community community) {
        return CollectionUtils.isNotEmpty(community.getAddresses())
                ? community.getAddresses().get(0).getStreetAddress()
                : null;
    }
}
