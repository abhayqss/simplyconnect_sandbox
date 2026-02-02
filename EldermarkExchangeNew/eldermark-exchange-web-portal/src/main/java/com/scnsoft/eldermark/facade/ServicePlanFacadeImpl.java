package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.StatusCountDto;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServicePlanFacadeImpl implements ServicePlanStatisticsFacade {

    @Autowired
    private ListAndItemConverter<ServicePlanCount, StatusCountDto> servicePlanCountDtoConverter;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public Long count() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return servicePlanService.count(permissionFilter);
    }

    @Override
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public List<StatusCountDto> countGroupedByStatus() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return servicePlanCountDtoConverter.convertList(servicePlanService.countGroupedByStatus(permissionFilter));
    }

}
