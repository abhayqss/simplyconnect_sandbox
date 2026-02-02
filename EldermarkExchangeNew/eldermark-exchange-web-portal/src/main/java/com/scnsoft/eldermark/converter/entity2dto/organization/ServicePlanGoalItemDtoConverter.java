package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanGoalItemDto;
import com.scnsoft.eldermark.entity.serviceplan.ReferralServiceRequestStatus;
import com.scnsoft.eldermark.entity.serviceplan.ReferralServiceStatus;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ServicePlanGoalItemDtoConverter implements ListAndItemConverter<ServicePlanGoal, ServicePlanGoalItemDto> {

    @Override
    public ServicePlanGoalItemDto convert(ServicePlanGoal source) {

        ServicePlanGoalItemDto target = new ServicePlanGoalItemDto();
        target.setId(source.getId());
        target.setGoal(source.getGoal());
        target.setBarriers(source.getBarriers());
        target.setInterventionAction(source.getInterventionAction());
        target.setResourceName(source.getResourceName());
        target.setTargetCompletionDate(DateTimeUtils.toEpochMilli(source.getTargetCompletionDate()));
        target.setCompletionDate(DateTimeUtils.toEpochMilli(source.getCompletionDate()));
        target.setGoalCompletion(source.getGoalCompletion());
        target.setProviderName(source.getProviderName());
        target.setProviderEmail(source.getProviderEmail());
        target.setProviderPhone(source.getProviderPhone());
        target.setOngoingService(source.getOngoingService());
        target.setContactName(source.getContactName());
        target.setServiceCtrlReqStatusId(Optional.ofNullable(source.getReferralServiceRequestStatus())
                .map(ReferralServiceRequestStatus::getRequestStatusId).orElse(null));
        target.setServiceStatusId(Optional.ofNullable(source.getReferralServiceStatus()).map(ReferralServiceStatus::getStatusId).orElse(null));
        target.setProviderAddress(source.getProviderAddress());
        target.setWasPreviouslyInPlace(source.getWasPreviouslyInPlace());
        return target;
    }
}
