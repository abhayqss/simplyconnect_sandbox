package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.dto.serviceplan.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.List;

public interface ClientServicePlanFacade {

    Page<ClientServicePlanListItemDto> find(ServicePlanFilter filter, Pageable pageable);

    ClientDashboardServicePlanDto findInDevelopmentForDashboard(Long clientId);

    ServicePlanDto findById(Long serviceId);

    List<IdentifiedNamedEntityDto> getDomains(Long id);

    Long add(ServicePlanDto servicePlanDto);

    Long edit(ServicePlanDto servicePlanDto);

    Page<ServicePlanHistoryDto> findHistoryById(Long servicePlanId, Pageable pageRequest);

    boolean existsUnarchivedInDevelopment(Long clientId);

    void writeServicePlanPDFToResponse(Long servicePlanId, List<Long> domainIds, HttpServletResponse response, ZoneId zoneId);

    Long count(ServicePlanFilter filter);

    boolean canAdd(Long clientId);

    boolean canView();

    ServicePlanDateDto findServicePlanForStatusCheck(Long clientId);

    List<ServicePlanResourceNameDto> findServicePlanForStatusCheckResourceNames(Long clientId);

    ServicePlanDto findInDevelopment(Long clientId);
}
