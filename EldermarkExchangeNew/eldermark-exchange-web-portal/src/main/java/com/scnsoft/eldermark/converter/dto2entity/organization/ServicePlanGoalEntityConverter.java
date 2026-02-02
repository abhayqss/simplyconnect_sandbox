package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanGoalItemDto;
import com.scnsoft.eldermark.entity.serviceplan.ReferralServiceRequestStatus;
import com.scnsoft.eldermark.entity.serviceplan.ReferralServiceStatus;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanGoalEntityConverter implements ListAndItemConverter<ServicePlanGoalItemDto, ServicePlanGoal> {

    @Override
    public ServicePlanGoal convert(ServicePlanGoalItemDto servicePlanGoalItemDto) {
        ServicePlanGoal servicePlanGoal = new ServicePlanGoal();

        //id will be used to resolve chain id for goal and new entity will be created. ServicePlan is Auditable
        servicePlanGoal.setId(servicePlanGoalItemDto.getId());
        servicePlanGoal.setGoal(servicePlanGoalItemDto.getGoal());
        servicePlanGoal.setBarriers(servicePlanGoalItemDto.getBarriers());
        servicePlanGoal.setInterventionAction(servicePlanGoalItemDto.getInterventionAction());
        servicePlanGoal.setResourceName(servicePlanGoalItemDto.getResourceName());
        servicePlanGoal.setCompletionDate(DateTimeUtils.toInstant(servicePlanGoalItemDto.getCompletionDate()));
        servicePlanGoal.setTargetCompletionDate(DateTimeUtils.toInstant(servicePlanGoalItemDto.getTargetCompletionDate()));
        servicePlanGoal.setGoalCompletion(servicePlanGoalItemDto.getGoalCompletion());
        servicePlanGoal.setProviderName(servicePlanGoalItemDto.getProviderName());
        servicePlanGoal.setProviderEmail(servicePlanGoalItemDto.getProviderEmail());
        servicePlanGoal.setProviderPhone(servicePlanGoalItemDto.getProviderPhone());
        servicePlanGoal.setOngoingService(servicePlanGoalItemDto.isOngoingService());
        servicePlanGoal.setContactName(servicePlanGoalItemDto.getContactName());
        servicePlanGoal.setReferralServiceRequestStatus(ReferralServiceRequestStatus
                .findByRequestStatusId(servicePlanGoalItemDto.getServiceCtrlReqStatusId()));
        servicePlanGoal.setReferralServiceStatus(ReferralServiceStatus.findByStatusId(servicePlanGoalItemDto.getServiceStatusId()));
        servicePlanGoal.setProviderAddress(servicePlanGoalItemDto.getProviderAddress());
        servicePlanGoal.setWasPreviouslyInPlace(servicePlanGoalItemDto.getWasPreviouslyInPlace());
        return servicePlanGoal;
    }

}
