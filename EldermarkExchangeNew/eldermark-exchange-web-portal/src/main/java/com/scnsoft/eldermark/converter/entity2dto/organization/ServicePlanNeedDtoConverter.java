package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanGoalItemDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanEducationNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanNeedDtoConverter implements ListAndItemConverter<ServicePlanNeed, ServicePlanNeedDto> {

    @Autowired
    private ListAndItemConverter<ServicePlanGoal, ServicePlanGoalItemDto> servicePlanGoalDtoConverter;

    @Override
    public ServicePlanNeedDto convert(ServicePlanNeed source) {
        ServicePlanNeedDto target = new ServicePlanNeedDto();
        target.setId(source.getId());
        target.setPriorityId(source.getPriority().getNumberPriority());
        target.setPriorityName(source.getPriority().getDisplayName().toUpperCase());
        target.setDomainId(source.getDomain().getDomainNumber());
        target.setDomainName(source.getDomain().name());
        if (source.getProgramType() != null) {
            target.setProgramTypeId(source.getProgramType().getId());
            target.setProgramTypeName(source.getProgramType().getCode());
        }
        if (source.getProgramSubType() != null) {
            target.setProgramSubTypeId(source.getProgramSubType().getId());
            target.setProgramSubTypeName(source.getProgramSubType().getCode());
        }


        if (source instanceof ServicePlanEducationNeed) {
            final ServicePlanEducationNeed servicePlanEducationNeed = (ServicePlanEducationNeed) source;
            target.setActivationOrEducationTask(servicePlanEducationNeed.getActivationOrEducationTask());
            target.setTargetCompletionDate(DateTimeUtils.toEpochMilli(servicePlanEducationNeed.getTargetCompletionDate()));
            target.setCompletionDate(DateTimeUtils.toEpochMilli(servicePlanEducationNeed.getCompletionDate()));
        } else if (source instanceof ServicePlanGoalNeed) {
            final ServicePlanGoalNeed servicePlanGoalNeed = (ServicePlanGoalNeed) source;
            target.setNeedOpportunity(servicePlanGoalNeed.getNeedOpportunity());
            target.setProficiencyGraduationCriteria(servicePlanGoalNeed.getProficiencyGraduationCriteria());
            if (CollectionUtils.isNotEmpty(servicePlanGoalNeed.getGoals())) {
                target.setGoals(servicePlanGoalDtoConverter.convertList(servicePlanGoalNeed.getGoals()));
            }
        }
        return target;
    }

}
