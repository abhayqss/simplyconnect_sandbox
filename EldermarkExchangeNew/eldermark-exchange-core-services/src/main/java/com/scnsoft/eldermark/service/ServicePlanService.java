package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ServicePlanSecurityAwareEntity;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public interface ServicePlanService extends
        AuditableEntityService<ServicePlan>,
        SecurityAwareEntityService<ServicePlanSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    ServicePlan getOne(Long servicePlanId);

    Page<ServicePlan> find(ServicePlanFilter clientServicePlanFilter, PermissionFilter permissionFilter, Pageable pageable);

    Page<ServicePlan> findHistoryById(Long servicePlanId, Pageable pageRequest);

    boolean existsUnarchivedInDevelopmentForClient(Long clientId);

    boolean existsUnarchivedInDevelopmentForClientExceptFor(Long clientId, Long servicePlanId);

    DocumentReport getServicePlanPDF(Long servicePlanId, List<Long> domainIds, ZoneId zoneId);

    Long count(PermissionFilter permissionFilter);

    Long count(ServicePlanFilter clientServicePlanFilter, PermissionFilter permissionFilter);

    List<ServicePlanCount> countGroupedByStatus(PermissionFilter permissionFilter);

    Optional<ServicePlan> findSingleOrThrowIfMultiple(ServicePlanFilter clientServicePlanFilter, PermissionFilter permissionFilter);

    ServicePlanGoal findGoal(Long goalId);

    Optional<ServicePlan> findLatestSharedWithClient(Long clientId);

    boolean existsUnarchivedSharedWithClientForClient(Long clientId);

    <P> List<P> find(Specification<ServicePlan> specification, Class<P> projectionClass);
}
