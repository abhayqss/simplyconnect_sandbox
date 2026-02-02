package com.scnsoft.eldermark.services.transformer.serviceplan.entity2dto;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanListItemDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanListItemTransformer extends ListAndItemTransformer<ServicePlan, ServicePlanListItemDto> {
    @Override
    public ServicePlanListItemDto convert(ServicePlan servicePlan) {
        if (servicePlan == null) {
            return null;
        }
        ServicePlanListItemDto servicePlanListItemDto = new ServicePlanListItemDto();
        servicePlanListItemDto.setId(servicePlan.getId());
        servicePlanListItemDto.setStatus(servicePlan.getServicePlanStatus() != null ? servicePlan.getServicePlanStatus().getDisplayName() : null);
        servicePlanListItemDto.setDateCreated(servicePlan.getDateCreated());
        servicePlanListItemDto.setDateCompleted(servicePlan.getDateCompleted());
        servicePlanListItemDto.setDateModified(servicePlan.getLastModifiedDate());
        servicePlanListItemDto.setScoring(servicePlan.getScoring().getTotalScore());
        servicePlanListItemDto.setAuthor(servicePlan.getEmployee().getFullName());
        return servicePlanListItemDto;
    }
}
