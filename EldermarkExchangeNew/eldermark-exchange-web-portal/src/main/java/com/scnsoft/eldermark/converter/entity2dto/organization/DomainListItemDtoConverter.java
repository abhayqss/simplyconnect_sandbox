package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.DomainDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;

@Component
public class DomainListItemDtoConverter implements ListAndItemConverter<ServicePlanNeedType, DomainDto> {

	@Override
	public DomainDto convert(ServicePlanNeedType source) {
		DomainDto target = new DomainDto();
		target.setId(Long.valueOf(source.getDomainNumber()));
		target.setTitle(source.getDisplayName());
		target.setName(source.name());
		return target;
	}

}
