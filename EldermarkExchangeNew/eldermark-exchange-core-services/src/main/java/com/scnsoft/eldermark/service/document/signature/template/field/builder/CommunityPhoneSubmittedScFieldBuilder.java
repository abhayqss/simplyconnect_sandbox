package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.util.Normalizer;
import org.springframework.stereotype.Component;

@Component
class CommunityPhoneSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityPhoneSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_PHONE);
    }

    @Override
    protected String extractValue(Community community) {
        return community.getPhone() != null
                ? "+" + Normalizer.normalizePhone(community.getPhone())
                : null;
    }
}
