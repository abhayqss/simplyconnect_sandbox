package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.LabSecurityFieldsAware;

import java.util.Collection;

public interface LabSecurityService {

    boolean canAdd(LabSecurityFieldsAware dto);

    boolean canAddToCommunity(Long communityId);

    boolean canViewList(Long organizationId);

    boolean canView(Long labOrderId);

    boolean canReview(Long labOrderId);

    boolean canViewLabs();

    boolean canReviewInOrganization(Long organizationId);

    boolean canReview(Collection<Long> labOrderIds);

    boolean canViewResults(Long labOrderId);
}
