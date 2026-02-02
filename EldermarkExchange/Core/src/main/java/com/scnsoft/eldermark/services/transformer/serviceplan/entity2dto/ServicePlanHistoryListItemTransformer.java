package com.scnsoft.eldermark.services.transformer.serviceplan.entity2dto;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanHistoryListItemDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanHistoryListItemTransformer extends ListAndItemTransformer<ServicePlan, ServicePlanHistoryListItemDto> {

    @Override
    public ServicePlanHistoryListItemDto convert(ServicePlan servicePlan) {
        if (servicePlan == null) {
            return null;
        }
        ServicePlanHistoryListItemDto servicePlanHistoryListItem = new ServicePlanHistoryListItemDto();
        servicePlanHistoryListItem.setId(servicePlan.getId());
        servicePlanHistoryListItem.setDateModified(servicePlan.getLastModifiedDate());
        servicePlanHistoryListItem.setStatus(servicePlan.getStatus() != null ? servicePlan.getStatus().getDisplayName() : null);
        servicePlanHistoryListItem.setAuthor(servicePlan.getEmployee().getFullName() + ", " + servicePlan.getEmployee().getCareTeamRole().getName());
        return servicePlanHistoryListItem;
    }

}
