package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.Marketplace;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceDao extends AppJpaRepository<Marketplace, Long> {

    Marketplace findByOrganizationIdAndCommunityIdIsNull(Long organizationId);

    List<Marketplace> findByOrganizationId(Long organizationId);

    Marketplace findByCommunityId(Long communityId);

    List<Marketplace> findAllByMarketplacePartnerNetworks_partnerNetworkId(Long partnerNetworkId);

}
