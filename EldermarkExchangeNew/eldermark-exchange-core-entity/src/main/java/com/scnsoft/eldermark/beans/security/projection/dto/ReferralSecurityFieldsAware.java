package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

import java.util.List;

public interface ReferralSecurityFieldsAware extends ClientIdAware {

    List<Long> ANY_SERVICES = List.of(-1L);

    Long getReferringCommunityId();

    List<Long> getSharedCommunityIds();

    Long getMarketplaceCommunityId();

    List<Long> getServices();
}
