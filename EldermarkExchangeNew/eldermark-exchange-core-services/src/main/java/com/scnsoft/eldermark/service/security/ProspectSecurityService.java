package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware;

public interface ProspectSecurityService {

    Long ANY_TARGET_COMMUNITY = -1L;

    boolean canViewList();

    boolean canView(Long prospectId);

    boolean canAdd(ProspectSecurityFieldsAware prospect);

    boolean canEdit(Long prospectId);
    boolean canEditSsn(Long prospectId);
}
