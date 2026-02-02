package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import org.springframework.stereotype.Component;

@Component
class CommunityNameSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityNameSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_NAME);
    }

    @Override
    protected String extractValue(Community community) {
        return community.getName();
    }
}
