package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.CommunityDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author phomal
 * Created on 1/30/2018.
 */
@Service
@Transactional(readOnly = true)
public class CommunitiesService {

    private final CareCoordinationCommunityDao ccCommunityDao;
    private final PrivilegesService privilegesService;

    @Autowired
    public CommunitiesService(CareCoordinationCommunityDao ccCommunityDao, PrivilegesService privilegesService) {
        this.ccCommunityDao = ccCommunityDao;
        this.privilegesService = privilegesService;
    }

    public List<CommunityDto> listByOrganization(Long orgId) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return convert(ccCommunityDao.findByDatabaseId(orgId));
    }

    public List<CommunityDto> listAllAccessible() {
        final List<Organization> communities = privilegesService.listCommunitiesWithReadAccess();
        final Collection<Organization> result = new TreeSet<>(new Comparator<Organization>() {
            @Override
            public int compare(Organization o1, Organization o2) {
                return ObjectUtils.compare(o1.getId(), o2.getId());
            }
        });
        result.addAll(communities);

        final List<Long> orgIds = privilegesService.listOrganizationIdsWithReadAccess();
        if (!CollectionUtils.isEmpty(orgIds)) {
            final List<Organization> communities2 = ccCommunityDao.findByDatabaseIdIn(orgIds);
            result.addAll(communities2);
        }

        return convert(result);
    }

    public CommunityDto get(Long communityId) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return convert(ccCommunityDao.getOne(communityId));
    }

    private static List<CommunityDto> convert(Collection<Organization> organizations) {
        final List<CommunityDto> dtoList = new ArrayList<>(organizations.size());
        for (Organization organization : organizations) {
            dtoList.add(convert(organization));
        }
        return dtoList;
    }

    private static CommunityDto convert(Organization organization) {
        final CommunityDto dto = new CommunityDto();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setOrgId(organization.getDatabaseId());
        dto.setOrgName(organization.getDatabase().getName());
        return dto;
    }

}
