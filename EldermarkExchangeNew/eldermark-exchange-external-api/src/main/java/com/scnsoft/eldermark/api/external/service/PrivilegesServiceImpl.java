package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PrivilegesDao;
import com.scnsoft.eldermark.api.external.entity.Privilege;
import com.scnsoft.eldermark.api.external.specification.CommunityExtApiSpecifications;
import com.scnsoft.eldermark.api.external.utils.ApiSecurityUtils;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PrivilegesServiceImpl implements PrivilegesService {

    private final PrivilegesDao privilegesDao;
    private final CommunityDao communityDao;
    private final CommunityExtApiSpecifications communityExtApiSpecifications;
    private final CommunitySpecificationGenerator communitySpecifications;

    @Autowired
    public PrivilegesServiceImpl(PrivilegesDao privilegesDao, CommunityDao communityDao, CommunityExtApiSpecifications communitySpecifications, CommunitySpecificationGenerator communitySpecifications1) {
        this.privilegesDao = privilegesDao;
        this.communityDao = communityDao;
        this.communityExtApiSpecifications = communitySpecifications;
        this.communitySpecifications = communitySpecifications1;
    }

    @Override
    public Boolean canReadOrganization(Long organizationId) {
        if (organizationId == null) {
            return Boolean.FALSE;
        }
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasRight(userAppId, Privilege.Name.ORGANIZATION_READ, organizationId);
    }

    @Override
    public Boolean canReadCommunity(Long communityId) {
        if (communityId == null) {
            return Boolean.FALSE;
        }
        return hasOrganizationOrCommunityAccess(communityId) || hasConsanaAccessToCommunity(communityId);
    }

    private Boolean hasOrganizationOrCommunityAccess(Long communityId) {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasRight(userAppId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId);
    }

//    public Boolean canManageNucleusData() {
//        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
//        return privilegesDao.hasRight(userAppId, Privilege.Name.SPECIAL_NUCLEUS, (Long) null);
//    }

    @Override
    public List<Organization> listOrganizationsWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listOrganizationsByPrivilege(userAppId, Privilege.Name.ORGANIZATION_READ);
    }

    @Override
    public List<Long> listOrganizationIdsWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listOrganizationIdsByPrivilege(userAppId, Privilege.Name.ORGANIZATION_READ);
    }

    @Override
    public List<Community> listCommunitiesWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listCommunitiesByPrivilege(userAppId, Privilege.Name.COMMUNITY_READ);
    }

    private boolean hasConsanaAccessToCommunity(Long facilityId) {
        if (!hasConsanaAccess()) {
            return false;
        }

        var byId = communitySpecifications.byCommunityIdsEligibleForDiscovery(Collections.singletonList(facilityId));
        var enabledIntegration = communityExtApiSpecifications.consanaSyncEnabled(true);
        return communityDao.exists(byId.and(enabledIntegration));
    }

    @Override
    public boolean hasConsanaAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasUserRight(userAppId, Privilege.Name.SPECIAL_CONSANA);
    }
}
