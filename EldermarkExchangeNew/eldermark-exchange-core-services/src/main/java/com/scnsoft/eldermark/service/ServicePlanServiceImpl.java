package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ServicePlanSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ServicePlanDao;
import com.scnsoft.eldermark.dao.ServicePlanGoalDao;
import com.scnsoft.eldermark.dao.ServicePlanNeedDao;
import com.scnsoft.eldermark.dao.specification.ServicePlanSpecificationGenerator;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ServicePlanServiceImpl extends BaseAuditableService<ServicePlan> implements ServicePlanService {

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Autowired
    private ServicePlanGoalDao servicePlanGoalDao;

    @Autowired
    private ServicePlanPdfGenerationService servicePlanPdfGenerationService;

    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecificationGenerator;

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Override
    public ServicePlan getOne(Long servicePlanId) {
        return servicePlanDao.getOne(servicePlanId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicePlan> find(ServicePlanFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilter = servicePlanSpecificationGenerator.byFilter(filter);
        var isUnarchived = servicePlanSpecificationGenerator.isUnarchived();
        var hasAccess = servicePlanSpecificationGenerator.hasAccess(permissionFilter);

        return servicePlanDao.findAll(byFilter.and(isUnarchived).and(hasAccess), pageable);
    }

    @Override
    @Transactional
    public ServicePlan save(ServicePlan entity) {
        return servicePlanDao.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public void updateRelatedTrackedEntities(ServicePlan servicePlan) {
        var needs = CollectionUtils.emptyIfNull(servicePlan.getNeeds());
        var goals = ServicePlanUtils.getGoals(servicePlan);

        //needs' and goals' ids are coming from users, therefore need to verify that they
        //are indeed related to edited service plan.
        validateNeedsRelation(servicePlan, needs);
        validateGoalsRelation(servicePlan, goals);

        updateNeedsForHistory(needs);
        updateGoalsForHistory(goals);
    }

    private void validateNeedsRelation(ServicePlan servicePlan, Collection<ServicePlanNeed> needs) {
        if (!needs.stream()
                .filter(need -> need.getId() != null)
                .allMatch(need -> need.getServicePlan().getId().equals(servicePlan.getId()))) {
            throw new BusinessException(BusinessExceptionType.NOT_RELATED_ENTITIES);
        }
    }

    private void validateGoalsRelation(ServicePlan servicePlan, Collection<ServicePlanGoal> goals) {
        if (!goals.stream()
                .filter(goal -> goal.getId() != null)
                .allMatch(goal -> goal.getNeed().getServicePlan().getId().equals(servicePlan.getId()))) {
            throw new BusinessException(BusinessExceptionType.NOT_RELATED_ENTITIES);
        }
    }

    private void updateNeedsForHistory(Collection<ServicePlanNeed> needs) {
        needs.stream()
                .filter(need -> need.getId() != null)
                .forEach(need -> {
                    var chainId = ObjectUtils.firstNonNull(servicePlanNeedDao.findChainId(need.getId()), need.getId());
                    need.setChainId(chainId);
                    need.setId(null);
                });
    }

    private void updateGoalsForHistory(Collection<ServicePlanGoal> goals) {
        goals.stream()
                .filter(goal -> goal.getId() != null)
                .forEach(goal -> {
                    var chainId = ObjectUtils.firstNonNull(servicePlanGoalDao.findChainId(goal.getId()), goal.getId());
                    goal.setChainId(chainId);
                    goal.setId(null);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServicePlan findById(Long id) {
        return servicePlanDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicePlan> findHistoryById(Long servicePlanId, Pageable pageRequest) {
        var historyById = servicePlanSpecificationGenerator.historyById(servicePlanId);

        Page<ServicePlan> servicePlans = servicePlanDao.findAll(historyById, pageRequest);
        return servicePlans;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUnarchivedInDevelopmentForClient(Long clientId) {
        return servicePlanDao.existsByClientIdAndArchivedIsFalseAndServicePlanStatus(clientId, ServicePlanStatus.IN_DEVELOPMENT);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUnarchivedInDevelopmentForClientExceptFor(Long clientId, Long servicePlanId) {
        return servicePlanDao.existsByClientIdAndArchivedIsFalseAndServicePlanStatusAndIdNot(clientId, ServicePlanStatus.IN_DEVELOPMENT, servicePlanId);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentReport getServicePlanPDF(Long servicePlanId, List<Long> domainIds, ZoneId zoneId) {
        var servicePlan = servicePlanDao.findById(servicePlanId).orElseThrow();
        try {
            return servicePlanPdfGenerationService.generatePdfReport(servicePlan, domainIds, zoneId);
        } catch (DocumentException e) {
            throw new InternalServerException(InternalServerExceptionType.PDF_GENERATION_ERROR);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(PermissionFilter permissionFilter) {
        var unarchivedSpecification = servicePlanSpecificationGenerator.isUnarchived();
        var accessSpecification = servicePlanSpecificationGenerator.hasAccess(permissionFilter);
        return servicePlanDao.count(unarchivedSpecification.and(accessSpecification));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePlanCount> countGroupedByStatus(PermissionFilter permissionFilter) {
        var unarchivedSpecification = servicePlanSpecificationGenerator.isUnarchived();
        var accessSpecification = servicePlanSpecificationGenerator.hasAccess(permissionFilter);

        return servicePlanDao.countGroupedByStatus(unarchivedSpecification.and(accessSpecification));
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(ServicePlanFilter clientServicePlanFilter, PermissionFilter permissionFilter) {
        var byFilter = servicePlanSpecificationGenerator.byFilter(clientServicePlanFilter);
        var isUnarchived = servicePlanSpecificationGenerator.isUnarchived();
        var hasAccess = servicePlanSpecificationGenerator.hasAccess(permissionFilter);
        return servicePlanDao.count((byFilter.and(isUnarchived).and(hasAccess)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServicePlan> findSingleOrThrowIfMultiple(ServicePlanFilter clientServicePlanFilter, PermissionFilter permissionFilter) {
        var byFilter = servicePlanSpecificationGenerator.byFilter(clientServicePlanFilter);
        var isUnarchived = servicePlanSpecificationGenerator.isUnarchived();
        var hasAccess = servicePlanSpecificationGenerator.hasAccess(permissionFilter);
        var result = servicePlanDao.findAll((byFilter.and(isUnarchived).and(hasAccess)));
        if (CollectionUtils.isNotEmpty(result) && result.size() == 1) {
            return Optional.of(result.get(0));
        } else if (CollectionUtils.isNotEmpty(result) && result.size() > 1) {
            throw new BusinessException("Client has more than 1 In Development Service Plan");
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ServicePlanGoal findGoal(Long goalId) {
        return servicePlanGoalDao.findById(goalId).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServicePlan> findLatestSharedWithClient(Long clientId) {
        var isUnarchived = servicePlanSpecificationGenerator.isUnarchived();
        var byClientIdAndMerged = servicePlanSpecificationGenerator.byClientIdWithMerged(clientId);
        var byStatus = servicePlanSpecificationGenerator.byStatus(ServicePlanStatus.SHARED_WITH_CLIENT);
        var pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, ServicePlan_.DATE_CREATED));
        return servicePlanDao.findAll(byClientIdAndMerged.and(isUnarchived).and(byStatus), pageable).get().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUnarchivedSharedWithClientForClient(Long clientId) {
        var isUnarchived = servicePlanSpecificationGenerator.isUnarchived();
        var byClientIdAndMerged = servicePlanSpecificationGenerator.byClientIdWithMerged(clientId);
        var byStatus = servicePlanSpecificationGenerator.byStatus(ServicePlanStatus.SHARED_WITH_CLIENT);
        return servicePlanDao.exists(byClientIdAndMerged.and(isUnarchived).and(byStatus));
    }

    @Override
    public <P> List<P> find(Specification<ServicePlan> specification, Class<P> projectionClass) {
        return servicePlanDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicePlanSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return servicePlanDao.findById(id, ServicePlanSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePlanSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return servicePlanDao.findByIdIn(ids, ServicePlanSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return servicePlanDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return servicePlanDao.findByIdIn(ids, projection);
    }
}
