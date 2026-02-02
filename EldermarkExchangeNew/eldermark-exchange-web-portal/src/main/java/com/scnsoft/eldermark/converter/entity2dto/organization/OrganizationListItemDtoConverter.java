package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.OrganizationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedViewableEntityDto;
import com.scnsoft.eldermark.entity.DatabaseOrgCountEntity;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.security.OrganizationSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrganizationListItemDtoConverter implements Converter<Organization, OrganizationListItemDto> {

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public OrganizationListItemDto convert(Organization source) {
        var target = new OrganizationListItemDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setCommunityCount(Optional.ofNullable(source.getDatabaseOrgCountEntity()).map(DatabaseOrgCountEntity::getOrgHieCount).orElse(0L));
        target.setAffiliatedOrganizations(organizationService.findAffiliatedOrganizations(source.getId()).stream()
            .map(affiliatedOrg -> new IdentifiedNamedViewableEntityDto(affiliatedOrg.getAffiliatedOrganizationId(), affiliatedOrg.getAffiliatedOrganizationName(),
                    organizationSecurityService.canView(affiliatedOrg.getAffiliatedOrganizationId())))
            .collect(Collectors.toList()));
        target.setCreatedAutomatically(source.getCreatedAutomatically());
        target.setLastModified(DateTimeUtils.toEpochMilli(source.getLastModified()));
        target.setCanEdit(organizationSecurityService.canEdit(source.getId()));
        return target;
    }
}
