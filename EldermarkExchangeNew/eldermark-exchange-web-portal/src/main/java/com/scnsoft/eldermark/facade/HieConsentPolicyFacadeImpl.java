package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.hiepolicy.HieConsentPolicyDto;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.HieConsentPolicyUpdateService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HieConsentPolicyFacadeImpl implements HieConsentPolicyFacade {

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Override
    @PreAuthorize("@clientHieConsentPolicySecurityService.canEdit(#dto.clientId)")
    public void update(HieConsentPolicyDto dto) {
        var client = clientService.findById(dto.getClientId());
        hieConsentPolicyUpdateService.updateHieConsentPolicyByClient(
                client,
                dto.getValue(),
                HieConsentPolicySource.WEB,
                loggedUserService.getCurrentEmployee()
        );
    }
}
