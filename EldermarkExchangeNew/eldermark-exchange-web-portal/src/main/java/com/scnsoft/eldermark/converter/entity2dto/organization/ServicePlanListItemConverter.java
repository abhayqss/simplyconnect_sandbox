package com.scnsoft.eldermark.converter.entity2dto.organization;

import java.time.Instant;
import java.util.Optional;

import com.scnsoft.eldermark.service.security.ServicePlanSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.dto.serviceplan.ClientServicePlanListItemDto;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;

@Component
public class ServicePlanListItemConverter implements ListAndItemConverter<ServicePlan, ClientServicePlanListItemDto> {

	@Autowired
	private ServicePlanSecurityService servicePlanSecurityService;

	@Override
	public ClientServicePlanListItemDto convert(ServicePlan source) {
		ClientServicePlanListItemDto target = new ClientServicePlanListItemDto();
		String servicePlanStatus = source.getServicePlanStatus() != null
				? source.getServicePlanStatus().getDisplayName()
				: null;
		if (servicePlanStatus != null) {
			TypeDto status = new TypeDto();
			status.setName(servicePlanStatus);
			status.setTitle(servicePlanStatus);
			target.setStatus(status);
		}
		target.setId(source.getId());
		target.setDateCreated(source.getDateCreated().toEpochMilli());
		target.setDateCompleted(Optional.ofNullable(source.getDateCompleted()).map(Instant::toEpochMilli).orElse(null));
		target.setScoring(source.getScoring() != null ? source.getScoring().getTotalScore() : null);
		target.setAuthor(source.getEmployee().getFullName());
		target.setClientId(source.getClient().getId());
		target.setClientName(source.getClient().getFullName());
		target.setCanEdit(servicePlanSecurityService.canEdit(source.getId()));
		return target;
	}
}
