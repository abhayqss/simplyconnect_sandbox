package com.scnsoft.eldermark.beans.projection;

import java.util.Set;

public interface DocumentSignatureTemplateSecurityFieldsAware {
    Set<Long> getCommunityIds();

    Set<Long> getOrganizationIds();
}
