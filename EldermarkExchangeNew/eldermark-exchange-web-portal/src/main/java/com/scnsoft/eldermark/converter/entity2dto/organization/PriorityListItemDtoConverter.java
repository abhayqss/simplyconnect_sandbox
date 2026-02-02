package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.PriorityDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedPriority;

@Component
public class PriorityListItemDtoConverter implements ListAndItemConverter<ServicePlanNeedPriority, PriorityDto> {

	@Override
	public PriorityDto convert(ServicePlanNeedPriority source) {	
		PriorityDto target = new PriorityDto();
		target.setId(Long.valueOf(source.getNumberPriority()));
		target.setTitle(source.getDisplayName());
		target.setName(source.getDisplayName().replaceAll(" ", "_").toUpperCase());
		return target;
	}

}
