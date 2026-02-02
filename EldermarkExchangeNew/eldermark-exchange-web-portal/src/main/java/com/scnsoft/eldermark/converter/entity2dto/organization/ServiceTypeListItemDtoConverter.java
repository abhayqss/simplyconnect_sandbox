package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ServiceTypeListItemDto;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.service.ServiceTypeService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ServiceTypeListItemDtoConverter extends BaseServiceTypeConverter<ServiceType, ServiceTypeListItemDto> {

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    protected Converter<ServiceType, ServiceTypeListItemDto> getItemConverter() {

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return source -> new ServiceTypeListItemDto(
                source.getId(),
                source.getDisplayName(),
                source.getServiceCategoryId(),
                source.getServiceCategory().getDisplayName(),
                source.getCanAdditionalClinicalInfoBeShared(),
                serviceTypeService.isAllowedToCreateClientReferral(source, permissionFilter),
                serviceTypeService.isAllowedToCreateB2bReferral(source, permissionFilter)
        ) ;
    }
}
