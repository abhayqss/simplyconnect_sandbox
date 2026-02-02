package com.scnsoft.eldermark.services.carecoordination;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.serviceplan.ServicePlanDao;
import com.scnsoft.eldermark.dao.serviceplan.ServicePlanGoalDao;
import com.scnsoft.eldermark.dao.serviceplan.ServicePlanNeedDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.services.carecoordination.pdf.ServicePlanPdfGenerator;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanHistoryListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ServicePlanServiceImpl extends AuditableEntityServiceImpl<ServicePlan, ServicePlanDto> implements ServicePlanService {

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Autowired
    private ServicePlanGoalDao servicePlanGoalDao;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private Converter<ServicePlan, ServicePlanListItemDto> servicePlanListItemDtoConverter;

    @Autowired
    private ListAndItemTransformer<ServicePlan, ServicePlanDto> servicePlanDtoTransformer;

    @Autowired
    private ListAndItemTransformer<ServicePlanDto, ServicePlan> servicePlanEntityTransformer;

    @Autowired
    private ListAndItemTransformer<ServicePlan, ServicePlanHistoryListItemDto> servicePlanHistoryListItemDtoConverter;

    @Autowired
    private ServicePlanPdfGenerator servicePlanPdfGenerator;

    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;

    @Override
    public Page<ServicePlanListItemDto> listPatientServicePlans(Long patientId, String search, Pageable pageRequest) {
        Boolean canView = canViewServicePlans(patientId);
        if (!canView) {
            return new PageImpl<>(new ArrayList<ServicePlanListItemDto>(), pageRequest, 0);
        }
        Page<ServicePlan> servicePlans;
        Set<Long> matchedResidentIds = getMatchedResidentIds(patientId);
        if (StringUtils.isEmpty(search)) {
            servicePlans = servicePlanDao.getAllByResident_IdInAndArchivedIsFalse(matchedResidentIds, pageRequest);
        } else {
            Integer scoringSearch = CareCoordinationUtils.tryParseInteger(search);
            Date dateSearch = CareCoordinationUtils.tryParseDate(search);
            if (scoringSearch != null) {
                servicePlans = servicePlanDao.getAllByResidentIdAndTotalScore(matchedResidentIds, scoringSearch, pageRequest);
            } else if (dateSearch != null) {
                servicePlans = servicePlanDao.getAllByResidentIdWithSearchByDate(matchedResidentIds, dateSearch, pageRequest);
            } else {
                String[] searchParts = search.split("\\s+");
                String firstSearchPart = "%" + searchParts[0] + "%";
                String secondSearchPart = searchParts.length != 1 ? "%" + searchParts[searchParts.length - 1] + "%" : "";
                servicePlans = servicePlanDao.getAllByResidentIdWithSearchByAuthorAndStatus(matchedResidentIds, firstSearchPart, secondSearchPart, pageRequest);
            }
        }
        Page<ServicePlanListItemDto> result = servicePlans.map(servicePlanListItemDtoConverter);
        setEditableStatus(matchedResidentIds, result);
        return result;
    }

    @Override
    public Long count(Long patientId) {
        Boolean canView = canViewServicePlans(patientId);
        if (!canView) {
            return 0l;
        }
        return servicePlanDao.countByResident_IdInAndArchivedIsFalse(getMatchedResidentIds(patientId));
    }

    @Override
    public Long save(ServicePlanDto servicePlanDto, Long patientId) {
        Resident resident = residentDao.get(patientId);
        ServicePlan servicePlan = servicePlanEntityTransformer.convert(servicePlanDto);
        servicePlan.setEmployee(SecurityUtils.getAuthenticatedUser().getEmployee());
        servicePlan.setResident(resident);
        if (servicePlanDto.getId() == null) {
            createAuditableEntityWithoutPostCreate(servicePlan);
        } else {
            updateAuditableEntityWithoutPostCreate(servicePlan);
        }
        return servicePlan.getId();
    }

    @Override
    protected void updateRelatedTrackedEntities(ServicePlan servicePlan) {
        List<ServicePlanNeed> needs = ObjectUtils.firstNonNull(servicePlan.getNeeds(), Collections.<ServicePlanNeed>emptyList());
        List<ServicePlanGoal> goals = getGoals(servicePlan);

        //needs' and goals' ids are coming from users, therefore need to verify that they
        //are indeed related to edited service plan.
        validateNeedsRelation(servicePlan, needs);
        validateGoalsRelation(servicePlan, goals);

        updateNeedsForHistory(needs);
        updateGoalsForHistory(goals);
    }

    private static List<ServicePlanGoal> getGoals(ServicePlan servicePlan) {
        List<ServicePlanGoal> result = new ArrayList<>();

        List<ServicePlanNeed> needs = servicePlan.getNeeds();
        if (CollectionUtils.isNotEmpty(needs)) {
            for (ServicePlanNeed need : servicePlan.getNeeds()) {
                if (need instanceof ServicePlanGoalNeed) {
                    List<ServicePlanGoal> goals = ((ServicePlanGoalNeed) need).getGoals();
                    if (CollectionUtils.isNotEmpty(goals)) {
                        result.addAll(goals);
                    }
                }
            }
        }

        return result;
    }

    private void validateNeedsRelation(ServicePlan servicePlan, List<ServicePlanNeed> needs) {
        if (needs == null) {
            return;
        }
        for (ServicePlanNeed need : needs) {
            if (need.getId() != null && !servicePlan.getId().equals(need.getServicePlan().getId())) {
                throw new BusinessException("Not related entities");
            }
        }
    }

    private void validateGoalsRelation(ServicePlan servicePlan, List<ServicePlanGoal> goals) {
        if (goals == null) {
            return;
        }
        for (ServicePlanGoal goal : goals) {
            if (goal.getId() != null && !servicePlan.getId().equals(goal.getNeed().getServicePlan().getId())) {
                throw new BusinessException("Not related entities");
            }
        }
    }

    private void updateNeedsForHistory(List<ServicePlanNeed> needs) {
        if (CollectionUtils.isNotEmpty(needs)) {
            for (ServicePlanNeed need : needs) {
                Long chainId = ObjectUtils.firstNonNull(servicePlanNeedDao.findChainId(need.getId()), need.getId());
                need.setChainId(chainId);
                need.setId(null);
            }
        }
    }

    private void updateGoalsForHistory(List<ServicePlanGoal> goals) {
        if (CollectionUtils.isNotEmpty(goals)) {
            for (ServicePlanGoal goal : goals) {
                Long chainId = ObjectUtils.firstNonNull(servicePlanGoalDao.findChainId(goal.getId()), goal.getId());
                goal.setChainId(chainId);
                goal.setId(null);
            }
        }
    }

    @Override
    public ServicePlanDto getServicePlanDetails(Long servicePlanId) {
        ServicePlan servicePlan = servicePlanDao.findOne(servicePlanId);
        return servicePlanDtoTransformer.convert(servicePlan);
    }

    @Override
    public ByteArrayOutputStream generatePdf(ServicePlan servicePlan, Long timeZoneOffset) throws DocumentException, IOException {
        return servicePlanPdfGenerator.generate(servicePlan, timeZoneOffset);
    }

    @Override
    public ServicePlan getServicePlan(Long servicePlanId) {
        return servicePlanDao.getOne(servicePlanId);
    }

    @Override
    public Boolean isNewServicePlanCanBeAddedForPatient(Long patientId) {
        Long inDevelopmentServicePlansCount = servicePlanDao.countByResident_IdInAndServicePlanStatusAndArchivedIsFalse(getMatchedResidentIds(patientId), ServicePlanStatus.IN_DEVELOPMENT);
        return inDevelopmentServicePlansCount == 0l;
    }

    @Override
    public Page<ServicePlanHistoryListItemDto> listServicePlanHistory(Long servicePlanId, Pageable pageRequest) {
        final ServicePlan sp = servicePlanDao.getOne(servicePlanId);
        final Long searchId = sp.getChainId() != null ? sp.getChainId() : servicePlanId;
        Page<ServicePlan> servicePlans = servicePlanDao.getServicePlanHistory(searchId, pageRequest);
        return servicePlans.map(servicePlanHistoryListItemDtoConverter);
    }

    private Set<Long> getMatchedResidentIds(Long patientId) {
        final Set<Long> mergedFilterResidentsIds = new HashSet<Long>();
        mergedFilterResidentsIds.add(patientId);
        mergedFilterResidentsIds.addAll(mpiService.listMergedResidents(patientId));
        return mergedFilterResidentsIds;
    }

    private Boolean canViewServicePlans(Long patientId) {
        Set<Long> loggedEmployeeIdsForPatient = careCoordinationResidentService.getLoggedEmployeeIdsAvailableForPatient(patientId);
        Boolean viewable = false;
        if (!CollectionUtils.isEmpty(loggedEmployeeIdsForPatient)) {
            for (Long employeeId : loggedEmployeeIdsForPatient) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                viewable = viewable || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_VIEW_SERVICE_PLANS);
            }
        }
        return viewable;
    }

    private void setEditableStatus(Set<Long> matchedResidentIds, Page<ServicePlanListItemDto> result) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            for (ServicePlanListItemDto servicePlanListItemDto : result.getContent()) {
                servicePlanListItemDto.setEditable(Boolean.TRUE);
            }
        } else {
            for (Long matchedPatientId : matchedResidentIds) {
                Set<Long> loggedEmployeeIdsForPatient = careCoordinationResidentService.getLoggedEmployeeIdsAvailableForPatient(matchedPatientId);
                if (!CollectionUtils.isEmpty(loggedEmployeeIdsForPatient)) {
                    boolean orgAdmin = false;
                    for (Long employeeId : loggedEmployeeIdsForPatient) {
                        Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                        if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR)) {
                            orgAdmin = true;
                            break;
                        }
                    }
                    Set<Long> editableServicePlanIdsForPatient;
                    if (orgAdmin) {
                        editableServicePlanIdsForPatient = servicePlanDao.getServicePlanIdsForPatient(matchedPatientId);
                    } else {
                        editableServicePlanIdsForPatient = servicePlanDao.getServicePlanIdsForPatientCreatedByEmployeeIds(matchedPatientId, loggedEmployeeIdsForPatient);
                    }
                    if (CollectionUtils.isNotEmpty(editableServicePlanIdsForPatient)) {
                        for (ServicePlanListItemDto servicePlanListItemDto : result.getContent()) {
                            if (editableServicePlanIdsForPatient.contains(servicePlanListItemDto.getId()))
                                servicePlanListItemDto.setEditable(Boolean.TRUE);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected ServicePlan save(ServicePlan entity) {
        return servicePlanDao.save(entity);
    }

    @Override
    protected ServicePlan findById(Long id) {
        return servicePlanDao.getOne(id);
    }

    @Override
    protected ServicePlan dtoToEntity(ServicePlanDto servicePlanDto) {
        return servicePlanEntityTransformer.convert(servicePlanDto);
    }

    @Override
    protected void postCreate(ServicePlan entity, ServicePlanDto servicePlanDto) {
    }
}
