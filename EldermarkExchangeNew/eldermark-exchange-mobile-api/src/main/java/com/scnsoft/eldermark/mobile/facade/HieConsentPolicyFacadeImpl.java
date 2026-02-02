package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.mobile.dto.ClientHieConsentPolicyDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.HieConsentPolicyUpdateService;
import com.scnsoft.eldermark.service.security.ClientHieConsentPolicySecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HieConsentPolicyFacadeImpl implements HieConsentPolicyFacade {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Autowired
    private ClientHieConsentPolicySecurityService clientHieConsentPolicySecurityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private Converter<Client, ClientHieConsentPolicyDto> clientHieConsentPolicyDtoConverter;

    @Override
    @PreAuthorize("@clientHieConsentPolicySecurityService.canEdit(#dto.clientId)")
    public void update(ClientHieConsentPolicyDto dto) {
        var client = clientService.findById(dto.getClientId());

        hieConsentPolicyUpdateService.updateHieConsentPolicyByClient(
                client,
                dto.getHieConsentPolicy(),
                HieConsentPolicySource.MOBILE,
                loggedUserService.getCurrentEmployee()
        );
    }

    @Override
    @PreAuthorize("@clientHieConsentPolicySecurityService.canView()")
    public List<ClientHieConsentPolicyDto> findAll() {
        var currentEmployee = loggedUserService.getCurrentEmployee();
        var clientIds = currentEmployee.getAssociatedClientIds();

        return clientService.findAllById(clientIds).stream()
                .map(clientHieConsentPolicyDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return clientHieConsentPolicySecurityService.canView();
    }
}
