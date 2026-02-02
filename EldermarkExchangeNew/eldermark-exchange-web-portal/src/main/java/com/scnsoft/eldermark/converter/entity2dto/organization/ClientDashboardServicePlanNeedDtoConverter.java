package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanGoalDto;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanEducationNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientDashboardServicePlanNeedDtoConverter implements ListAndItemConverter<ServicePlanNeed, ClientDashboardServicePlanNeedDto> {

    @Autowired
    private ListAndItemConverter<ServicePlanGoal, ClientDashboardServicePlanGoalDto> clientDashboardServicePlanGoalDtoConverter;

    @Override
    public ClientDashboardServicePlanNeedDto convert(ServicePlanNeed source) {
        var target = new ClientDashboardServicePlanNeedDto();
        target.setDomainId(source.getDomain().getDomainNumber());
        target.setDomainName(source.getDomain().name());
        target.setDomainTitle(source.getDomain().getDisplayName());
        target.setPriorityId(source.getPriority().getNumberPriority());
        if (source instanceof ServicePlanGoalNeed) {
            var servicePlanGoalNeed = (ServicePlanGoalNeed) source;
            target.setTitle(servicePlanGoalNeed.getNeedOpportunity());
            target.setGoals(clientDashboardServicePlanGoalDtoConverter.convertList(servicePlanGoalNeed.getGoals()));
            if (CollectionUtils.isNotEmpty(target.getGoals()) && target.getGoals().size() > 1) {
                target.setGoals(target.getGoals().stream()
                        .sorted(Comparator.comparingInt(goalDto -> Optional.ofNullable(goalDto.getCompletion()).orElse(0)))
                        .collect(Collectors.toList()));
            }
        } else if (source instanceof ServicePlanEducationNeed) {
            var servicePlanGoalNeed = (ServicePlanEducationNeed) source;
            target.setTitle(servicePlanGoalNeed.getActivationOrEducationTask());
        }
        return target;
    }
}
