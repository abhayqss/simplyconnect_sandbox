package com.scnsoft.eldermark.services.transformer.serviceplan.dto2entity;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.GoalDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanGoalEntityTransformer extends ListAndItemTransformer<GoalDto, ServicePlanGoal> {

    @Override
    public ServicePlanGoal convert(GoalDto goalDto) {
        if (goalDto == null) {
            return null;
        }
        ServicePlanGoal servicePlanGoal = new ServicePlanGoal();
        servicePlanGoal.setId(goalDto.getId());
        servicePlanGoal.setGoal(goalDto.getGoal());
        servicePlanGoal.setBarriers(goalDto.getBarriers());
        servicePlanGoal.setInterventionAction(goalDto.getInterventionAction());
        servicePlanGoal.setResourceName(goalDto.getResourceName());
        servicePlanGoal.setGoalCompletion(goalDto.getProgress());
        servicePlanGoal.setCompletionDate(goalDto.getCompletionDate());
        servicePlanGoal.setTargetCompletionDate(goalDto.getTargetCompletionDate());
        return servicePlanGoal;
    }

}
