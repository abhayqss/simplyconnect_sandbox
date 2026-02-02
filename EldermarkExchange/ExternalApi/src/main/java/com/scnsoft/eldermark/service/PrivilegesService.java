package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.phr.PrivilegesDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.phr.Privilege;
import com.scnsoft.eldermark.services.predicates.ConsanaCommunityIntegrationEnabledPredicate;
import com.scnsoft.eldermark.web.security.ApiSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author phomal
 * Created on 1/18/2018
 */
@Service
@Transactional(readOnly = true)
public class PrivilegesService {

    private final PrivilegesDao privilegesDao;
    private final OrganizationDao organizationDao;
    private final ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate;

    @Autowired
    public PrivilegesService(PrivilegesDao privilegesDao, OrganizationDao organizationDao, ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate) {
        this.privilegesDao = privilegesDao;
        this.organizationDao = organizationDao;
        this.consanaCommunityIntegrationEnabledPredicate = consanaCommunityIntegrationEnabledPredicate;
    }

    public Boolean canReadOrganization(Long databaseId) {
        if (databaseId == null) {
            return Boolean.FALSE;
        }
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasRight(userAppId, Privilege.Name.ORGANIZATION_READ, databaseId);
    }

    public Boolean canReadCommunity(Long organizationId) {
        if (organizationId == null) {
            return Boolean.FALSE;
        }
        return hasOrganizationOrCommunityAccess(organizationId) || hasConsanaAccessToCommunity(organizationId);
    }

    private Boolean hasOrganizationOrCommunityAccess(Long organizationId) {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasRight(userAppId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId);
    }

    private boolean hasConsanaAccessToCommunity(Long facilityId) {
        return hasConsanaAccess() && consanaCommunityIntegrationEnabledPredicate.apply(organizationDao.get(facilityId));
    }

    public boolean hasConsanaAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasUserRight(userAppId, Privilege.Name.SPECIAL_CONSANA);
    }

    public Boolean canManageNucleusData() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.hasRight(userAppId, Privilege.Name.SPECIAL_NUCLEUS, (Long) null);
    }

    public List<Database> listOrganizationsWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listOrganizationsByPrivilege(userAppId, Privilege.Name.ORGANIZATION_READ);
    }

    public List<Long> listOrganizationIdsWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listOrganizationIdsByPrivilege(userAppId, Privilege.Name.ORGANIZATION_READ);
    }

    public List<Organization> listCommunitiesWithReadAccess() {
        final Long userAppId = ApiSecurityUtils.getCurrentUserId();
        return privilegesDao.listCommunitiesByPrivilege(userAppId, Privilege.Name.COMMUNITY_READ);
    }

}
