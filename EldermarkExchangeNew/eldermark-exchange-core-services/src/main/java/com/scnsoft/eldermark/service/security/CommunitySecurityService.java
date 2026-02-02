package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunitySecurityFieldsAware;

import java.util.Collection;

public interface CommunitySecurityService {

    boolean canAdd(CommunitySecurityFieldsAware dto);

    boolean canEdit(Long communityId);

    boolean canEditSignatureConfig(Long communityId);

    boolean canEditSignatureConfigInOrganization(Long organizationId);

    boolean canViewList();

    boolean canView(Long communityId);

    boolean canViewAll(Collection<Long> communityIds);

    boolean canViewByDeviceTypeId(Long deviceId);

    boolean hasAccessibleClient(Long communityId);

    boolean hasAccessibleClient(Collection<Long> communityIds);

    boolean canDownloadPicture(Long pictureId);

    boolean canViewLogo(Long communityId);
}
