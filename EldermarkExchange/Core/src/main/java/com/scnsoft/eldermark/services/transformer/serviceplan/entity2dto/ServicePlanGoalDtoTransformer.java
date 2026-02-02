package com.scnsoft.eldermark.services.transformer.serviceplan.entity2dto;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.GoalDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanGoalDtoTransformer extends ListAndItemTransformer<ServicePlanGoal, GoalDto> {
    @Override
    public GoalDto convert(ServicePlanGoal servicePlanGoal) {
        if (servicePlanGoal == null) {
            return null;
        }
        GoalDto goalDto = new GoalDto();
        goalDto.setId(servicePlanGoal.getId());
        goalDto.setGoal(servicePlanGoal.getGoal());
        goalDto.setBarriers(servicePlanGoal.getBarriers());
        goalDto.setInterventionAction(servicePlanGoal.getInterventionAction());
        goalDto.setResourceName(servicePlanGoal.getResourceName());
        goalDto.setProgress(servicePlanGoal.getGoalCompletion());
        goalDto.setTargetCompletionDate(servicePlanGoal.getTargetCompletionDate());
        goalDto.setCompletionDate(servicePlanGoal.getCompletionDate());
        return goalDto;
    }
}
