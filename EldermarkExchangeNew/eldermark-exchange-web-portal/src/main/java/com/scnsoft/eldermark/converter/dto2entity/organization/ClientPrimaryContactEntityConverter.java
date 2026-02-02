package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.ClientDto;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactAware;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactNotificationMethod;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientPrimaryContactEntityConverter implements Converter<ClientDto, ClientPrimaryContact> {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Override
    public ClientPrimaryContact convert(ClientDto source) {
        var sourcePrimaryContact = source.getPrimaryContact();
        ClientPrimaryContact targetPrimaryContact = null;
        if (sourcePrimaryContact != null) {
            if (source.getId() != null) {
                targetPrimaryContact = clientService.findById(source.getId(), ClientPrimaryContactAware.class).getPrimaryContact();
            }
            if (targetPrimaryContact == null) {
                targetPrimaryContact = new ClientPrimaryContact();
            }
            targetPrimaryContact.setType(ClientPrimaryContactType.valueOf(sourcePrimaryContact.getTypeName()));
            targetPrimaryContact.setNotificationMethod(ClientPrimaryContactNotificationMethod.valueOf(sourcePrimaryContact.getNotificationMethodName()));
            if (sourcePrimaryContact.getCareTeamMemberId() != null) {
                targetPrimaryContact.setClientCareTeamMember(clientCareTeamMemberService.getById(sourcePrimaryContact.getCareTeamMemberId()));
            } else {
                targetPrimaryContact.setClientCareTeamMember(null);
            }
        }
        return targetPrimaryContact;
    }
}
