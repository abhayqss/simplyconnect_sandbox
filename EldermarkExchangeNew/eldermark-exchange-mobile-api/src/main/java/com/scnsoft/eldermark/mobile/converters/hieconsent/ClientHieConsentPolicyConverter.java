package com.scnsoft.eldermark.mobile.converters.hieconsent;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.mobile.dto.ClientHieConsentPolicyDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientPrimaryContactDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.ClientHieConsentPolicySecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientHieConsentPolicyConverter implements Converter<Client, ClientHieConsentPolicyDto> {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientHieConsentPolicySecurityService clientHieConsentPolicySecurityService;

    @Autowired
    private Converter<ClientPrimaryContact, ClientPrimaryContactDto> clientPrimaryContactDtoConverter;

    @Override
    public ClientHieConsentPolicyDto convert(Client source) {
        var dto = new ClientHieConsentPolicyDto();
        dto.setClientId(source.getId());
        dto.setClientFullName(source.getFullName());
        dto.setClientCommunityName(source.getCommunity().getName());
        dto.setHieConsentPolicy(source.getHieConsentPolicyType());
        if (source.getPrimaryContactId() != null) {
            dto.setClientPrimaryContact(clientPrimaryContactDtoConverter.convert(source.getPrimaryContact()));
        }
        dto.setIsConfirmed(clientService.hasConfirmedHieConsentPolicy(source));
        dto.setCanEdit(clientHieConsentPolicySecurityService.canEdit(source.getId()));
        return dto;
    }
}
