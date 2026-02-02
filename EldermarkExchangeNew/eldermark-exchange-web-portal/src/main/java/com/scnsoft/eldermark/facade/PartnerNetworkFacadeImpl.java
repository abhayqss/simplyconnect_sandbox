package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.PartnerNetworkDetailsFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import com.scnsoft.eldermark.dto.PartnerNetworkOrganizationListItemDto;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import com.scnsoft.eldermark.service.PartnerNetworkService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartnerNetworkFacadeImpl implements PartnerNetworkFacade {

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Autowired
    private Converter<PartnerNetworkOrganization, PartnerNetworkOrganizationListItemDto> partnerNetworkOrganizationListItemDtoConverter;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@partnerNetworkSecurityService.canViewList()")
    public Collection<PartnerNetworkOrganizationListItemDto> findAllGroupedByOrganization(PartnerNetworkFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return partnerNetworkService.find(filter, permissionFilter)
                .stream()
                .map(network -> loadOrganizations(filter, network))
                .map(Page::getContent)
                .flatMap(List::stream)
                .collect(Collectors.toMap(PartnerNetworkOrganizationListItemDto::getId,
                        Function.identity(),
                        (existing, replacement) -> {
                            existing.getCommunities().addAll(replacement.getCommunities());
                            return existing;
                        },
                        HashMap::new))
                .values();
    }

    private Page<PartnerNetworkOrganizationListItemDto> loadOrganizations(PartnerNetworkFilter filter, PartnerNetwork network) {
        var detailFilter = new PartnerNetworkDetailsFilter();
        detailFilter.setPartnerNetworkId(network.getId());
        detailFilter.setExcludeCommunityId(filter.getCommunityId());
        detailFilter.setServiceIds(filter.getServiceIds());
        return partnerNetworkService.findGroupedByOrganization(detailFilter, PaginationUtils.setSort(Pageable.unpaged(), Sort.by(Organization_.NAME)))
                .map(partnerNetworkOrganizationListItemDtoConverter::convert);
    }
}
