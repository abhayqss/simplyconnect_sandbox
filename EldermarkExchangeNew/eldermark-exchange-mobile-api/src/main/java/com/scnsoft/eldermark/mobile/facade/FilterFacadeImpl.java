package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FilterFacadeImpl implements FilterFacade {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ListAndItemConverter<IdNameAware, IdentifiedNamedEntityDto> identifiedNamedEntityDtoConverter;

    @Override
    @PreAuthorize("@organizationSecurityService.canViewList()")
    public List<IdentifiedNamedEntityDto> findOrganizations() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var organizations = organizationService.findForFilter(new OrganizationFilter(), permissionFilter);
        return identifiedNamedEntityDtoConverter.convertList(organizations);
    }

    @Override
    @PreAuthorize("@communitySecurityService.canViewList()")
    public List<IdentifiedNamedEntityDto> findCommunities(Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var communities = communityService.findByOrgIdForFilter(permissionFilter, organizationId, false, IdNameAware.class);
        return identifiedNamedEntityDtoConverter.convertList(communities);
    }
}
