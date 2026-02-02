package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationFilterListItemAwareEntity;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.directory.DirOrganizationListItemDto;
import org.springframework.stereotype.Component;

@Component
public class OrganizationDirectoryConverter implements ListAndItemConverter<OrganizationFilterListItemAwareEntity, DirOrganizationListItemDto>{

	@Override
	public DirOrganizationListItemDto convert(OrganizationFilterListItemAwareEntity source) {
		var target = new DirOrganizationListItemDto();
		target.setId(source.getId());
		target.setLabel(source.getName());
		target.setAreLabsEnabled(source.getLabsEnabled());
		return target;
	}

}
