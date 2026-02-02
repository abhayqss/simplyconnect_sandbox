package com.scnsoft.eldermark.converter.entity2dto.client;

import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.dto.employee.EmployeeAssociatedClientDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeAssociatedClientDtoConverter implements Converter<Client, EmployeeAssociatedClientDto> {

    @Autowired
    private Converter<Client, PrimaryContactDto> primaryContactDtoConverter;

    @Autowired
    private ClientService clientService;

    @Override
    public EmployeeAssociatedClientDto convert(Client source) {
        var dto = new EmployeeAssociatedClientDto();

        dto.setId(source.getId());
        dto.setFullName(source.getFullName());
        dto.setCommunityName(source.getCommunity().getName());

        var policy = source.getHieConsentPolicyType();
        dto.setHieConsentPolicyName(policy);
        dto.setHieConsentPolicyTitle(policy.getDisplayName());

        if (source.getPrimaryContact() != null) {
            dto.setPrimaryContact(primaryContactDtoConverter.convert(source));
        }

        dto.setShouldConfirmHieConsentPolicy(!clientService.hasConfirmedHieConsentPolicy(source));

        return dto;
    }
}
