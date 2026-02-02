package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.serviceplan.BaseServicePlanDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDomainScoreDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public abstract class BaseClientServicePlanDtoConverter<T extends BaseServicePlanDto> implements Converter<ServicePlan, T> {

    private Converter<ServicePlanScoring, List<ServicePlanDomainScoreDto>> servicePlanDomainScoreDtoConverter;

    public BaseClientServicePlanDtoConverter(Converter<ServicePlanScoring, List<ServicePlanDomainScoreDto>> servicePlanDomainScoreDtoConverter) {
        this.servicePlanDomainScoreDtoConverter = servicePlanDomainScoreDtoConverter;
    }

    @Override
    public T convert(ServicePlan source) {
        var target = create();
        target.setId(source.getId());
        target.setDateCreated(DateTimeUtils.toEpochMilli(source.getDateCreated()));
        target.setDateCompleted(DateTimeUtils.toEpochMilli(source.getDateCompleted()));
        target.setScoring(servicePlanDomainScoreDtoConverter.convert(source.getScoring()));
        return target;
    }

    protected abstract T create();
}
