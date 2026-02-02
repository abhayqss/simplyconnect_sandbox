package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.OrgDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrgsServiceImpl implements OrgsService {

    private final OrganizationService organizationService;
    private final PrivilegesService privilegesService;

    @Autowired
    public OrgsServiceImpl(OrganizationService organizationService, PrivilegesService privilegesService) {
        this.organizationService = organizationService;
        this.privilegesService = privilegesService;
    }

    @Override
    public OrgDto get(Long orgId) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        var organization = organizationService.findById(orgId, IdNameAware.class);

        return convert(organization);
    }

    @Override
    public List<OrgDto> listAllAccessible() {
        var ids = privilegesService.listOrganizationIdsWithReadAccess();
        return convert(organizationService.findAllById(ids, IdNameAware.class));
    }

    private List<OrgDto> convert(List<IdNameAware> organizations) {
        return organizations.stream().map(this::convert).collect(Collectors.toList());
    }

    private OrgDto convert(IdNameAware organization) {
        final OrgDto dto = new OrgDto();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        return dto;
    }
}
