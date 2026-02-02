package com.scnsoft.eldermark.dao;


import com.scnsoft.eldermark.entity.MarketplacePartnerNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplacePartnerNetworkDao extends JpaRepository<MarketplacePartnerNetwork, Long> {

    boolean existsByMarketplaceId(Long marketplaceId);

}
