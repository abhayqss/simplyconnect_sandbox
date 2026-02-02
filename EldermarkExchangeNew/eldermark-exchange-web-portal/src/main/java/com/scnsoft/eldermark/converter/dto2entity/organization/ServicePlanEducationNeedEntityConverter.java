package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanEducationNeed;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanEducationNeedEntityConverter extends ServicePlanNeedEntityConverter<ServicePlanEducationNeed> implements Converter<ServicePlanNeedDto, ServicePlanEducationNeed> {

    @Override
    public ServicePlanEducationNeed convert(ServicePlanNeedDto needDto) {
        if (needDto == null) {
            return null;
        }

        ServicePlanEducationNeed servicePlanEducationNeed = new ServicePlanEducationNeed();

        servicePlanEducationNeed = setCommonFields(needDto, servicePlanEducationNeed);
        servicePlanEducationNeed.setCompletionDate(DateTimeUtils.toInstant(needDto.getCompletionDate()));
        servicePlanEducationNeed.setTargetCompletionDate(DateTimeUtils.toInstant(needDto.getTargetCompletionDate()));
        servicePlanEducationNeed.setActivationOrEducationTask(needDto.getActivationOrEducationTask());
        return servicePlanEducationNeed;
    }

}
