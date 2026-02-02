package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanGoalDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import org.springframework.stereotype.Component;

@Component
public class ClientDashboardServicePlanGoalDtoConverter implements ListAndItemConverter<ServicePlanGoal, ClientDashboardServicePlanGoalDto> {

    @Override
    public ClientDashboardServicePlanGoalDto convert(ServicePlanGoal source) {
        var target = new ClientDashboardServicePlanGoalDto();
        target.setTitle(source.getGoal());
        target.setCompletion(source.getGoalCompletion());
        return target;
    }
}
