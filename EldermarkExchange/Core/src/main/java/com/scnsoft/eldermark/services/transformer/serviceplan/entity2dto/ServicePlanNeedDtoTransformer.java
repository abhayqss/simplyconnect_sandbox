package com.scnsoft.eldermark.services.transformer.serviceplan.entity2dto;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanEducationNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.GoalDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.NeedDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanNeedDtoTransformer extends ListAndItemTransformer<ServicePlanNeed, NeedDto> {

    @Autowired
    private ListAndItemTransformer<ServicePlanGoal, GoalDto> servicePlanGoalDtoTransformer;

    @Override
    public NeedDto convert(ServicePlanNeed servicePlanNeed) {
        if (servicePlanNeed == null) {
            return null;
        }
        NeedDto needDto = new NeedDto();
        needDto.setId(servicePlanNeed.getId());
        needDto.setPriority(servicePlanNeed.getPriority().getDisplayName());
        needDto.setType(servicePlanNeed.getType().getDisplayName());
        if (servicePlanNeed instanceof ServicePlanEducationNeed) {
            final ServicePlanEducationNeed servicePlanEducationNeed = (ServicePlanEducationNeed)servicePlanNeed;
            needDto.setActivationOrEducationTask(servicePlanEducationNeed.getActivationOrEducationTask());
            needDto.setTargetCompletionDate(servicePlanEducationNeed.getTargetCompletionDate());
            needDto.setCompletionDate(servicePlanEducationNeed.getCompletionDate());
        } else if (servicePlanNeed instanceof ServicePlanGoalNeed) {
            final ServicePlanGoalNeed servicePlanGoalNeed = (ServicePlanGoalNeed)servicePlanNeed;
            needDto.setNeedOpportunity(servicePlanGoalNeed.getNeedOpportunity());
            needDto.setProficiencyGraduationCriteria(servicePlanGoalNeed.getProficiencyGraduationCriteria());
            if (CollectionUtils.isNotEmpty(servicePlanGoalNeed.getGoals())) {
                needDto.setGoals(servicePlanGoalDtoTransformer.convertList(servicePlanGoalNeed.getGoals()));
            }
        }
        return needDto;
    }
}
