package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanDto;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanNeedDto;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanDomainScoreDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientDashboardServicePlanDtoConverter extends BaseClientServicePlanDtoConverter<ClientDashboardServicePlanDto> {

    @Autowired
    private ListAndItemConverter<ServicePlanNeed, ClientDashboardServicePlanNeedDto> clientDashboardServicePlanNeedDtoConverter;

    @Autowired
    public ClientDashboardServicePlanDtoConverter(Converter<ServicePlanScoring, List<ServicePlanDomainScoreDto>> servicePlanDomainScoreDtoConverter) {
        super(servicePlanDomainScoreDtoConverter);
    }

    @Override
    public ClientDashboardServicePlanDto convert(ServicePlan source) {
        var target = super.convert(source);
        target.setStatusName(source.getServicePlanStatus().name());
        target.setStatusTitle(source.getServicePlanStatus().getDisplayName());
        target.setNeeds(clientDashboardServicePlanNeedDtoConverter.convertList(source.getNeeds()));
        return target;
    }

    @Override
    protected ClientDashboardServicePlanDto create() {
        return new ClientDashboardServicePlanDto();
    }
}
