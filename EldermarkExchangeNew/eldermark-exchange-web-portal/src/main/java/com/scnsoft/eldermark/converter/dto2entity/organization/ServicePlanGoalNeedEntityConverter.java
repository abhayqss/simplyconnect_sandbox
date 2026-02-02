package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.serviceplan.ServicePlanGoalItemDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServicePlanGoalNeedEntityConverter extends ServicePlanNeedEntityConverter<ServicePlanGoalNeed> implements Converter<ServicePlanNeedDto, ServicePlanGoalNeed> {

    @Autowired
    private Converter<ServicePlanGoalItemDto, ServicePlanGoal> servicePlanGoalEntityConverter;

    @Override
    public ServicePlanGoalNeed convert(ServicePlanNeedDto servicePlanNeedDto) {
        ServicePlanGoalNeed servicePlanGoalNeed = new ServicePlanGoalNeed();

        servicePlanGoalNeed = setCommonFields(servicePlanNeedDto, servicePlanGoalNeed);
        servicePlanGoalNeed.setNeedOpportunity(servicePlanNeedDto.getNeedOpportunity());
        servicePlanGoalNeed.setProficiencyGraduationCriteria(servicePlanNeedDto.getProficiencyGraduationCriteria());

        List<ServicePlanGoal> goalList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(servicePlanNeedDto.getGoals())) {
            for (ServicePlanGoalItemDto servicePlanGoalItemDto : servicePlanNeedDto.getGoals()) {
                ServicePlanGoal servicePlanGoal = servicePlanGoalEntityConverter.convert(servicePlanGoalItemDto);
                servicePlanGoal.setNeed(servicePlanGoalNeed);
                goalList.add(servicePlanGoal);
            }
        }
        servicePlanGoalNeed.setGoals(goalList);
        return servicePlanGoalNeed;
    }
}
