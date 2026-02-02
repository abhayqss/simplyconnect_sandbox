package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientCareTeamMemberDtoToEntityConverter extends CareTeamMemberEntityConverter<ClientCareTeamMember> implements Converter<CareTeamMemberDto, ClientCareTeamMember> {

	@Autowired
	private ClientCareTeamMemberService clientCareTeamMemberService;

	@Autowired
	private ClientService clientService;

	@Override
	public ClientCareTeamMember convert(CareTeamMemberDto source) {
		ClientCareTeamMember target;
		if (source.getId() != null) {
			target = clientCareTeamMemberService.findById(source.getId()).orElseThrow();
		} else {
			target = new ClientCareTeamMember();
		}
		target = setCommonFields(source, target);
		target.setClient(clientService.findById(source.getClientId()));
		target.setClientId(source.getClientId());
		target.setIncludeInFaceSheet(source.getIncludeInFaceSheet());
		return target;
	}

}
