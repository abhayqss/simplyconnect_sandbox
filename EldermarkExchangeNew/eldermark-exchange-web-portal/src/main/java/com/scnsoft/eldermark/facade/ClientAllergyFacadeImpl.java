package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import com.scnsoft.eldermark.dto.AllergyDto;
import com.scnsoft.eldermark.dto.AllergyListItemDto;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import com.scnsoft.eldermark.service.ClientAllergyService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientAllergyFacadeImpl implements ClientAllergyFacade {

    @Autowired
    private Converter<ClientAllergy, AllergyDto> allergyDtoConverter;

    @Autowired
    private Converter<ClientAllergy, AllergyListItemDto> allergyListItemDtoConverter;

    @Autowired
    private ClientAllergyService clientAllergyService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAllergySecurityService.canViewList() && " +
            "@clientAllergySecurityService.canViewOfClientIfPresent(#filter)")
    public Long count(ClientAllergyFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAllergyService.count(filter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAllergySecurityService.canView(#id) ")
    public AllergyDto findById(@P("id") Long id) {
        return allergyDtoConverter.convert(clientAllergyService.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAllergySecurityService.canViewList() && " +
            "@clientAllergySecurityService.canViewOfClientIfPresent(#filter)")
    public Page<AllergyListItemDto> find(@P("filter") ClientAllergyFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAllergyService.find(filter, permissionFilter, PaginationUtils.applyEntitySort(pageable, AllergyListItemDto.class))
                .map(allergyListItemDtoConverter::convert);
    }
}
