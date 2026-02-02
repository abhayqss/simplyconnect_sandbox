package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDomainScoreDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanNeedDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServicePlanDtoConverter extends BaseClientServicePlanDtoConverter<ServicePlanDto> implements ListAndItemConverter<ServicePlan, ServicePlanDto> {

    @Autowired
    private ListAndItemConverter<ServicePlanNeed, ServicePlanNeedDto> servicePlanNeedDtoConverter;

    @Autowired
    public ServicePlanDtoConverter(Converter<ServicePlanScoring, List<ServicePlanDomainScoreDto>> servicePlanDomainScoreDtoConverter) {
        super(servicePlanDomainScoreDtoConverter);
    }

    @Override
    public ServicePlanDto convert(ServicePlan source) {
        var target = super.convert(source);
        target.setCreatedBy(source.getEmployee().getFullName());
        if (CollectionUtils.isNotEmpty(source.getNeeds())) {
            target.setNeeds(servicePlanNeedDtoConverter.convertList(source.getNeeds()));
        }
        target.setIsCompleted(ServicePlanStatus.SHARED_WITH_CLIENT.equals(source.getServicePlanStatus()));
        target.setClientId(source.getClient().getId());
        return target;
    }

    @Override
    protected ServicePlanDto create() {
        return new ServicePlanDto();
    }
}
