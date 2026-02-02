package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.PartnerNetworkDetailsFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.PartnerNetworkCommunitySecurityAwareEntity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartnerNetworkService {

    /**
     * IMPORTANT: make sure that only communities eligible for discovery are fetched for organizations with
     * all communities added to network.
     * <p>
     * If all communities for network it's better to use findByPartnerNetworkId
     *
     * @param filter
     * @param permissionFilter
     * @return
     */
    List<PartnerNetwork> find(PartnerNetworkFilter filter, PermissionFilter permissionFilter);

    List<PartnerNetworkCommunity> findByPartnerNetworkId(Long partnerNetworkId);

    List<PartnerNetworkCommunitySecurityAwareEntity> findSecurityByPartnerNetworkId(Long partnerNetworkId);

    Page<PartnerNetworkOrganization> findGroupedByOrganization(PartnerNetworkDetailsFilter filter, Pageable organizationPageable);

    boolean existsForCommunity(Long communityId);

    boolean isCommunityInNetwork(Long communityId, Long partnerNetworkId);

    boolean areInSameNetwork(Long... communityIds);

    List<PartnerNetwork> findNetworksWithAllCommunities(Long... communityIds);

}
