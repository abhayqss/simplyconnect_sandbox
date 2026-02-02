package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.StatusCountDto;

@Component
public class ServicePlanCountDtoConverter implements ListAndItemConverter<ServicePlanCount, StatusCountDto> {

    @Override
    public StatusCountDto convert(ServicePlanCount source) {
        StatusCountDto target = new StatusCountDto();
        target.setCount(source.getCount());
        target.setStatus(source.getStatus().getDisplayName());
        return target;
    }

}
