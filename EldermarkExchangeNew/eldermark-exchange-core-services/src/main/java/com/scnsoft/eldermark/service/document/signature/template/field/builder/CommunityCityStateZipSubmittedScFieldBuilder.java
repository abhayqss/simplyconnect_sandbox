package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import static com.scnsoft.eldermark.util.CareCoordinationUtils.concat;

@Component
class CommunityCityStateZipSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityCityStateZipSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_CITY_STATE_ZIP);
    }

    @Override
    protected String extractValue(Community community) {

        if (CollectionUtils.isEmpty(community.getAddresses())) {
            return null;
        }

        var address = community.getAddresses().get(0);
        var city = address.getCity();
        var state = address.getState();
        var postalCode = address.getPostalCode();

        
        return concat(", ", city, concat(" ", state, postalCode));
    }
}
