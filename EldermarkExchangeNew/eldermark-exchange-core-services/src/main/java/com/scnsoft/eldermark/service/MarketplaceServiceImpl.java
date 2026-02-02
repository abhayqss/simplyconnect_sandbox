package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.MarketplaceDao;
import com.scnsoft.eldermark.dao.MarketplacePartnerNetworkDao;
import com.scnsoft.eldermark.dao.specification.MarketplaceSpecificationGenerator;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class MarketplaceServiceImpl implements MarketplaceService {

    @Autowired
    private MarketplaceDao marketplaceDao;

    @Autowired
    private MarketplacePartnerNetworkDao partnersGroupDao;

    @Autowired
    private MarketplaceSpecificationGenerator marketplaceSpecificationGenerator;

    @Override
    public Page<Marketplace> find(MarketplaceFilter marketplaceFilter, PermissionFilter permissionFilter, Pageable pageable) {
        fillDefaultLocation(marketplaceFilter);

        var byFilterOrdered = marketplaceSpecificationGenerator.byFilterOrdered(marketplaceFilter, permissionFilter);
        var hasAccess = marketplaceSpecificationGenerator.hasAccess(permissionFilter);

        return find(byFilterOrdered.and(hasAccess),pageable);
    }

    @Override
    public Page<Marketplace> findPartners(Marketplace marketplace, MarketplaceFilter marketplaceFilter, PermissionFilter permissionFilter, Pageable pageable) {
        fillDefaultLocation(marketplaceFilter);

        var byFilterOrdered = marketplaceSpecificationGenerator.byFilterOrdered(marketplaceFilter, permissionFilter);

        Specification<Marketplace> partners;
        if (partnersGroupDao.existsByMarketplaceId(marketplace.getId())) {
            partners = marketplaceSpecificationGenerator.byPartnerNetwork(marketplace).and(marketplaceSpecificationGenerator.not(marketplace));
        } else {
            partners = marketplaceSpecificationGenerator.inOrganization(marketplace.getOrganizationId()).and(marketplaceSpecificationGenerator.not(marketplace));
        }

        return find(partners.and(byFilterOrdered),pageable);
    }

    @Override
    public Page<Marketplace> fetchServiceProviders(Marketplace marketplace, Pageable pageable) {
        Specification<Marketplace> partners;
        if (partnersGroupDao.existsByMarketplaceId(marketplace.getId())) {
            partners = marketplaceSpecificationGenerator.byPartnerNetwork(marketplace).and(marketplaceSpecificationGenerator.not(marketplace));
        } else {
            partners = marketplaceSpecificationGenerator.inOrganization(marketplace.getOrganizationId()).and(marketplaceSpecificationGenerator.not(marketplace));
        }
        return find(partners, pageable);
    }

    private void fillDefaultLocation(MarketplaceFilter marketplaceFilter) {
        if (marketplaceFilter != null && (marketplaceFilter.getLatitude() == null
                || marketplaceFilter.getLongitude() == null)) {
            marketplaceFilter.setLatitude(MN_LATITUDE);
            marketplaceFilter.setLongitude(MN_LONGITUDE);
        }
    }

    @Override
    public Marketplace findById(Long marketplaceId) {
        return marketplaceDao.findById(marketplaceId).orElseThrow();
    }

    @Override
    public Marketplace findByCommunityId(Long communityId) {
        return marketplaceDao.findByCommunityId(communityId);
    }

    @Override
    public <P> List<P> findAllByPartnerNetworkIds(Collection<Long> partnerNetworkIds, Class<P> projectionClass) {
        return marketplaceDao.findAll(
                marketplaceSpecificationGenerator.byPartnerNetworkIds(partnerNetworkIds),
                projectionClass
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Marketplace findByOrgId(Long id) {
        return marketplaceDao.findByOrganizationIdAndCommunityIdIsNull(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marketplace> findByOrgIdWithCommunities(Long id) {
        return marketplaceDao.findByOrganizationId(id);
    }

    @Override
    @Transactional
    public void save(Marketplace marketplace) {
        marketplaceDao.save(marketplace);
    }

    @Override
    @Transactional(readOnly = true)
    public IdAware findIdAwareByCommunityId(Long communityId) {
        var byCommunityId = marketplaceSpecificationGenerator.byCommunityId(communityId);
        return marketplaceDao.findAll(byCommunityId, IdAware.class).stream().findFirst().orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Marketplace> findSaved(PermissionFilter permissionFilter, Long employeeId, Pageable pageable) {
        var hasAccess = marketplaceSpecificationGenerator.hasAccess(permissionFilter);
        var savedByEmployeeId = marketplaceSpecificationGenerator.savedByEmployeeId(employeeId);
        return find(hasAccess.and(savedByEmployeeId),pageable);
    }

    public Page<Marketplace> find(Specification<Marketplace> specification, Pageable pageable) {
        return marketplaceDao.findAll(specification, pageable);
    }

    @Override
    public List<Marketplace> findAllById(Collection<Long> ids) {
        return marketplaceDao.findAllById(ids);
    }

    @Override
    public void saveAll(List<Marketplace> marketplaces) {
        marketplaceDao.saveAll(marketplaces);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsInNetworkMarketplaceAccessibleCommunities(PermissionFilter permissionFilter) {
        var employees = permissionFilter.getEmployees();
        var employeeOrganizationIds = CareCoordinationUtils.getOrganizationIdsSet(employees);
        var hasAccess = marketplaceSpecificationGenerator.hasAccess(permissionFilter);
        var partnerNetworkCommunities = marketplaceSpecificationGenerator.partnerNetworkCommunities(permissionFilter);
        var byOrganizationIdNot = marketplaceSpecificationGenerator.byOrganizationIdNotIn(employeeOrganizationIds);
        return marketplaceDao.exists(hasAccess.and(partnerNetworkCommunities).and(byOrganizationIdNot));
    }
}
