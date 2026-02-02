package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Marketplace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

public interface MarketplaceService {

    boolean DEFAULT_DISCOVERABLE = false;

    Double MN_LATITUDE = Double.valueOf(44.976073);
    Double MN_LONGITUDE = Double.valueOf(-93.272293);

    Page<Marketplace> find(MarketplaceFilter marketplaceFilter, PermissionFilter permissionFilter, Pageable pageable);

    Page<Marketplace> find(Specification<Marketplace> specification, Pageable pageable);

    Page<Marketplace> findPartners(Marketplace marketplace, MarketplaceFilter marketplaceFilter, PermissionFilter permissionFilter, Pageable pageable);

    Page<Marketplace> fetchServiceProviders(Marketplace marketplace, Pageable pageable);

    Marketplace findById(Long marketplaceId);

    void save(Marketplace marketplace);

    Marketplace findByOrgId(Long id);

    List<Marketplace> findByOrgIdWithCommunities(Long id);

    Marketplace findByCommunityId(Long communityId);

    <P> List<P> findAllByPartnerNetworkIds(Collection<Long> partnerNetworkIds, Class<P> projectionClass);

    IdAware findIdAwareByCommunityId(Long communityId);

    Page<Marketplace> findSaved(PermissionFilter permissionFilter, Long employeeId, Pageable pageable);

    List<Marketplace> findAllById(Collection<Long> ids);

    void saveAll(List<Marketplace> marketplaces);

    boolean existsInNetworkMarketplaceAccessibleCommunities(PermissionFilter permissionFilter);
}
