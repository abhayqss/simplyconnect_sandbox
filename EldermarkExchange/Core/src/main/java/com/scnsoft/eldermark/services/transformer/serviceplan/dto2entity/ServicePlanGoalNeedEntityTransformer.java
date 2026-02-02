package com.scnsoft.eldermark.services.transformer.serviceplan.dto2entity;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedPriority;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.GoalDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.NeedDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServicePlanGoalNeedEntityTransformer extends ListAndItemTransformer<NeedDto, ServicePlanGoalNeed> {

    @Autowired
    private ListAndItemTransformer<GoalDto, ServicePlanGoal> servicePlanGoalEntityTransformer;

    @Override
    public ServicePlanGoalNeed convert(NeedDto needDto) {
        if (needDto == null) {
            return null;
        }
        ServicePlanGoalNeed servicePlanGoalNeed = new ServicePlanGoalNeed();
        servicePlanGoalNeed.setId(needDto.getId());
        servicePlanGoalNeed.setNeedOpportunity(needDto.getNeedOpportunity());
        servicePlanGoalNeed.setProficiencyGraduationCriteria(needDto.getProficiencyGraduationCriteria());
        servicePlanGoalNeed.setType(ServicePlanNeedType.findByDisplayName(needDto.getType()));
        servicePlanGoalNeed.setPriority(ServicePlanNeedPriority.findByDisplayName(needDto.getPriority()));

        List<ServicePlanGoal> goalList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(needDto.getGoals())) {
            for (GoalDto goalDto : needDto.getGoals()) {
                ServicePlanGoal servicePlanGoal = servicePlanGoalEntityTransformer.convert(goalDto);
                servicePlanGoal.setNeed(servicePlanGoalNeed);
                goalList.add(servicePlanGoal);
            }
        }
        servicePlanGoalNeed.setGoals(goalList);
        return servicePlanGoalNeed;
    }
}
