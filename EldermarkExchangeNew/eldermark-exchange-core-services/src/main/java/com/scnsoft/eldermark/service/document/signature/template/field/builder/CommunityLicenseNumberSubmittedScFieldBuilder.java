package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import org.springframework.stereotype.Component;

@Component
class CommunityLicenseNumberSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityLicenseNumberSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_LICENSE_NUMBER);
    }

    @Override
    protected String extractValue(Community community) {
        return community.getLicenseNumber() != null
                ? "License # " + community.getLicenseNumber()
                : null;
    }
}
