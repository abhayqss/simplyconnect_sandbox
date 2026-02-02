package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAndCommunityIdAware;

public interface ContactSecurityFieldsAware extends OrganizationIdAndCommunityIdAware {

    Long getSystemRoleId();

}
