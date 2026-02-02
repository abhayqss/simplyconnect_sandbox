package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.CommunityDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommunitiesServiceImpl implements CommunitiesService {

    private final CommunityDao communityDao;
    private final PrivilegesService privilegesService;

    @Autowired
    public CommunitiesServiceImpl(CommunityDao communityDao, PrivilegesService privilegesService) {
        this.communityDao = communityDao;
        this.privilegesService = privilegesService;
    }

    @Override
    public List<CommunityDto> listByOrganization(Long orgId) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return convert(communityDao.findByOrganizationId(orgId));
    }

    @Override
    public List<CommunityDto> listAllAccessible() {
        var communities = privilegesService.listCommunitiesWithReadAccess();
        final Collection<Community> result = CareCoordinationUtils.idsComparingSet();
        result.addAll(communities);

        final List<Long> orgIds = privilegesService.listOrganizationIdsWithReadAccess();
        if (!CollectionUtils.isEmpty(orgIds)) {
            final List<Community> communities2 = communityDao.findByOrganizationIdIn(orgIds);
            result.addAll(communities2);
        }

        return convert(result);
    }

    @Override
    public CommunityDto get(Long communityId) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return convert(communityDao.getOne(communityId));
    }

    private static List<CommunityDto> convert(Collection<Community> communities) {
        return communities.stream().map(CommunitiesServiceImpl::convert).collect(Collectors.toList());
    }

    private static CommunityDto convert(Community community) {
        final CommunityDto dto = new CommunityDto();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setOrgId(community.getOrganizationId());
        dto.setOrgName(community.getOrganization().getName());
        return dto;
    }

}
