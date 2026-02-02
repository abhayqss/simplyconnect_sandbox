package com.scnsoft.eldermark.services.transformer.serviceplan.dto2entity;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanEducationNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedPriority;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.NeedDto;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanEducationNeedEntityTransformer extends ListAndItemTransformer<NeedDto, ServicePlanEducationNeed> {

    @Override
    public ServicePlanEducationNeed convert(NeedDto needDto) {
        if (needDto == null) {
            return null;
        }
        ServicePlanEducationNeed servicePlanEducationNeed = new ServicePlanEducationNeed();
        servicePlanEducationNeed.setId(needDto.getId());
        servicePlanEducationNeed.setCompletionDate(needDto.getCompletionDate());
        servicePlanEducationNeed.setTargetCompletionDate(needDto.getTargetCompletionDate());
        servicePlanEducationNeed.setActivationOrEducationTask(needDto.getActivationOrEducationTask());
        servicePlanEducationNeed.setType(ServicePlanNeedType.findByDisplayName(needDto.getType()));
        servicePlanEducationNeed.setPriority(ServicePlanNeedPriority.findByDisplayName(needDto.getPriority()));
        return servicePlanEducationNeed;
    }

}
