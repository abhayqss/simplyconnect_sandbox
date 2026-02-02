package com.scnsoft.eldermark.services.transformer.serviceplan.dto2entity;

import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.NeedDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ServicePlanEntityTransformer extends ListAndItemTransformer<ServicePlanDto, ServicePlan> {

    @Autowired
    private ListAndItemTransformer<ServicePlanDto, ServicePlanScoring> servicePlanScoringEntityTransformer;

    @Autowired
    private ListAndItemTransformer<NeedDto, ServicePlanEducationNeed> servicePlanEducationNeedEntityTransformer;

    @Autowired
    private ListAndItemTransformer<NeedDto, ServicePlanGoalNeed> servicePlanGoalNeedEntityTransformer;

    @Override
    public ServicePlan convert(ServicePlanDto servicePlanDto) {
        if (servicePlanDto == null) {
            return null;
        }
        ServicePlan servicePlan = new ServicePlan();
        servicePlan.setId(servicePlanDto.getId());
        servicePlan.setChainId(servicePlanDto.getChainId());
        servicePlan.setLastModifiedDate(new Date());
        servicePlan.setDateCreated(servicePlanDto.getDateCreated());
        if (servicePlanDto.getCompleted() == null || !servicePlanDto.getCompleted()) {
            servicePlan.setServicePlanStatus(ServicePlanStatus.IN_DEVELOPMENT);
            servicePlan.setDateCompleted(null);
        } else {
            servicePlan.setServicePlanStatus(ServicePlanStatus.SHARED_WITH_CLIENT);
            servicePlan.setDateCompleted(new Date());
        }

        ServicePlanScoring servicePlanScoring =  servicePlanScoringEntityTransformer.convert(servicePlanDto);
        if (servicePlanScoring != null) {
            servicePlanScoring.setServicePlan(servicePlan);
            servicePlan.setScoring(servicePlanScoring);
        }

        List<ServicePlanNeed> servicePlanNeedList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(servicePlanDto.getNeeds())) {
            for (NeedDto need : servicePlanDto.getNeeds()) {
                if (ServicePlanNeedType.EDUCATION_TASK.getDisplayName().equalsIgnoreCase(need.getType())) {
                    ServicePlanEducationNeed servicePlanEducationNeed = servicePlanEducationNeedEntityTransformer.convert(need);
                    servicePlanEducationNeed.setServicePlan(servicePlan);
                    servicePlanNeedList.add(servicePlanEducationNeed);
                } else {
                    ServicePlanGoalNeed servicePlanGoalNeed = servicePlanGoalNeedEntityTransformer.convert(need);
                    servicePlanGoalNeed.setServicePlan(servicePlan);
                    servicePlanNeedList.add(servicePlanGoalNeed);
                }
            }
        }
        servicePlan.setNeeds(servicePlanNeedList);

        return servicePlan;
    }

}
