package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class OrganizationLogoSubmittedScFieldBuilder extends AbstractCommunitySubmittedScFieldBuilder {

    public OrganizationLogoSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.ORGANIZATION_LOGO, SignatureSubmittedFieldType.IMAGE);
    }

    @Override
    protected String extractValue(Community community) {
        return Optional.ofNullable(community)
                .map(BasicEntity::getOrganization)
                .map(Organization::getMainLogoPath)
                .filter(it -> !it.isBlank())
                .orElse(null);
    }
}
