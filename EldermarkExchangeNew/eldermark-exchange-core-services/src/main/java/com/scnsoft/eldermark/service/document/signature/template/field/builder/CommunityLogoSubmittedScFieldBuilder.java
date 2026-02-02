package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CommunityLogoSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public CommunityLogoSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.COMMUNITY_LOGO, SignatureSubmittedFieldType.IMAGE);
    }

    @Override
    protected String extractValue(Community community) {
        return Optional.ofNullable(community)
                .map(Community::getMainLogoPath)
                .filter(it -> !it.isBlank())
                .orElse(null);
    }
}
