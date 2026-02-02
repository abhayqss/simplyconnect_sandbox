package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.*;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.ServicePlanSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientServicePlanFacadeImpl implements ClientServicePlanFacade {

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private ListAndItemConverter<ServicePlan, ServicePlanDto> servicePlanDtoConverter;

    @Autowired
    private ListAndItemConverter<ServicePlan, ClientServicePlanListItemDto> servicePlanListItemConverter;

    @Autowired
    private Converter<ServicePlan, ClientDashboardServicePlanDto> clientDashboardServicePlanDtoConverter;

    @Autowired
    private Converter<ServicePlanDto, ServicePlan> servicePlanEntityConverter;

    @Autowired
    private ListAndItemConverter<ServicePlan, ServicePlanHistoryDto> servicePlanHistoryListItemConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ServicePlanSecurityService servicePlanSecurityService;

    @Autowired
    private ClientService clientService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public Page<ClientServicePlanListItemDto> find(ServicePlanFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return servicePlanService.find(filter, permissionFilter, pageable).map(servicePlanListItemConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public ClientDashboardServicePlanDto findInDevelopmentForDashboard(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        ServicePlanFilter filter = new ServicePlanFilter();
        filter.setClientId(clientId);
        filter.setStatus(ServicePlanStatus.IN_DEVELOPMENT);
        return servicePlanService.findSingleOrThrowIfMultiple(filter, permissionFilter)
                .map(clientDashboardServicePlanDtoConverter::convert)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canView(#servicePlanId)")
    public ServicePlanDto findById(@P("servicePlanId") Long servicePlanId) {
        var servicePlan = servicePlanService.findById(servicePlanId);
        return servicePlanDtoConverter.convert(servicePlan);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewByClientId(#clientId)")
    public boolean existsUnarchivedInDevelopment(@P("clientId") Long clientId) {
        return servicePlanService.existsUnarchivedInDevelopmentForClient(clientId);
    }

    @Override
    @Transactional
    @PreAuthorize("@servicePlanSecurityService.canAdd(#servicePlanDto)")
    public Long add(@P("servicePlanDto") ServicePlanDto servicePlanDto) {
        clientService.validateActive(servicePlanDto.getClientId());
        if (!servicePlanDto.getIsCompleted() && servicePlanService.existsUnarchivedInDevelopmentForClient(servicePlanDto.getClientId())) {
            throw new BusinessException("There is an active service plan. You can not create a new one until the active plan is completed.");
        }
        var servicePlan = servicePlanEntityConverter.convert(servicePlanDto);
        return servicePlanService.createAuditableEntity(servicePlan);
    }

    @Override
    @Transactional
    @PreAuthorize("@servicePlanSecurityService.canEdit(#servicePlanDto.id)")
    public Long edit(@P("servicePlanDto") ServicePlanDto servicePlanDto) {
        clientService.validateActive(servicePlanDto.getClientId());
        if (!servicePlanDto.getIsCompleted() && servicePlanService.existsUnarchivedInDevelopmentForClientExceptFor(servicePlanDto.getClientId(), servicePlanDto.getId())) {
            throw new BusinessException("There is an active service plan. You can not create a new one until the active plan is completed.");
        }
        var servicePlan = servicePlanEntityConverter.convert(servicePlanDto);
        return servicePlanService.updateAuditableEntity(servicePlan);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canView(#servicePlanId)")
    public Page<ServicePlanHistoryDto> findHistoryById(Long servicePlanId, Pageable pageable) {
        var servicePlan = servicePlanService.findHistoryById(servicePlanId, PaginationUtils.setHistorySort(pageable));
        return new PageImpl<>(servicePlanHistoryListItemConverter.convertList(servicePlan.getContent()), pageable,
                servicePlan.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canView(#servicePlanId)")
    public void writeServicePlanPDFToResponse(@P("servicePlanId") Long servicePlanId, List<Long> domainIds, HttpServletResponse response,
                                              ZoneId zoneId) {
        var report = servicePlanService.getServicePlanPDF(servicePlanId, domainIds, zoneId);
        WriterUtils.copyDocumentContentToResponse(response, report);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canView(#servicePlanId)")
    public List<IdentifiedNamedEntityDto> getDomains(Long servicePlanId) {
        ServicePlan servicePlan = servicePlanService.findById(servicePlanId);
        return servicePlan.getNeeds()
                .stream()
                .map(ServicePlanNeed::getDomain)
                .filter(StreamUtils.distinctByKey(ServicePlanNeedType::getDomainNumber))
                .map(domain -> new IdentifiedNamedEntityDto(domain.getDomainNumber(), domain.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public Long count(ServicePlanFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return servicePlanService.count(filter, permissionFilter);
    }

    @Override
    public boolean canAdd(Long clientId) {
        return servicePlanSecurityService.canAdd(() -> clientId);
    }

    @Override
    public boolean canView() {
        return servicePlanSecurityService.canViewList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public ServicePlanDateDto findServicePlanForStatusCheck(Long clientId) {
        var result = findStatusCheckSP(clientId);
        return result.map(servicePlan -> new ServicePlanDateDto(servicePlan.getId(), DateTimeUtils.toEpochMilli(servicePlan.getDateCreated()))).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public List<ServicePlanResourceNameDto> findServicePlanForStatusCheckResourceNames(Long clientId) {
        var result = findStatusCheckSP(clientId);
        if (!result.isPresent()) {
            return Collections.emptyList();
        }
        var servicePlan = result.get();
        return servicePlan.getNeeds().stream().filter(servicePlanNeed -> servicePlanNeed instanceof ServicePlanGoalNeed)
                .flatMap(servicePlanNeed -> ((ServicePlanGoalNeed) servicePlanNeed).getGoals().stream())
                .filter(servicePlanGoal -> StringUtils.isNotBlank(servicePlanGoal.getResourceName()) && BooleanUtils.isTrue(servicePlanGoal.getOngoingService()))
                .map(servicePlanGoal -> new ServicePlanResourceNameDto(servicePlanGoal.getResourceName(), servicePlanGoal.getProviderName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@servicePlanSecurityService.canViewList()")
    public ServicePlanDto findInDevelopment(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var filter = new ServicePlanFilter();
        filter.setClientId(clientId);
        filter.setStatus(ServicePlanStatus.IN_DEVELOPMENT);
        var servicePlan = servicePlanService.findSingleOrThrowIfMultiple(filter, permissionFilter);
        return servicePlan.map(servicePlanDtoConverter::convert).orElse(null);
    }

    private Optional<ServicePlan> findStatusCheckSP(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var filter = new ServicePlanFilter();
        filter.setClientId(clientId);
        filter.setStatus(ServicePlanStatus.IN_DEVELOPMENT);
        filter.setResourceNamePopulated(Boolean.TRUE);
        filter.setOngoingService(Boolean.TRUE);
        return servicePlanService.findSingleOrThrowIfMultiple(filter, permissionFilter);
    }
}
