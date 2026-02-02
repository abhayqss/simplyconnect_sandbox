package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.entity.marketplace.ServiceType;

import java.util.List;

public interface CommunityReferralConfigProjection {
    boolean isReceiveNonNetworkReferrals();
    List<ServiceType> getMarketplaceServiceTypes();
}
