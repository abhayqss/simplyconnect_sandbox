package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.PrimaryFocusAwareKeyValueDto;
import com.scnsoft.eldermark.entity.basic.DisplayablePrimaryFocusAwareEntity;

@Deprecated
@Component
public class PrimaryFocusAwareKeyValueDtoConverter implements ListAndItemConverter<DisplayablePrimaryFocusAwareEntity, PrimaryFocusAwareKeyValueDto> {

	@Override
	public PrimaryFocusAwareKeyValueDto convert(DisplayablePrimaryFocusAwareEntity displayablePrimaryFocusAwareEntity) {
		PrimaryFocusAwareKeyValueDto result = new PrimaryFocusAwareKeyValueDto();
		result.setId(displayablePrimaryFocusAwareEntity.getId());
		result.setLabel(displayablePrimaryFocusAwareEntity.getDisplayName());
		result.setPrimaryFocusId(displayablePrimaryFocusAwareEntity.getPrimaryFocusId());
		return result;
	}
}
