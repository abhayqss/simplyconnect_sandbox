package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDomainScoreDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ServicePlanDomainScoreDtoConverter implements Converter<ServicePlanScoring, List<ServicePlanDomainScoreDto>> {
    @Override
    public List<ServicePlanDomainScoreDto> convert(ServicePlanScoring source) {
        List<ServicePlanDomainScoreDto> result = new ArrayList<>();
        Arrays.stream(ServicePlanNeedType.values()).filter(servicePlanNeedType -> servicePlanNeedType != ServicePlanNeedType.EDUCATION_TASK).forEach(servicePlanNeedType ->
                result.add(new ServicePlanDomainScoreDto(servicePlanNeedType.getDomainNumber(), ServicePlanUtils.resolveScore(source, servicePlanNeedType)))
        );
        return result;
    }
}
