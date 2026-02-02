package com.scnsoft.eldermark.service.security;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("clientHieConsentPolicySecurityService")
public class ClientHieConsentPolicySecurityServiceImpl implements ClientHieConsentPolicySecurityService {

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public boolean canEdit(Long clientId) {
        return hasAssociatedClient(clientId);
    }

    @Override
    public boolean canView() {
        return hasAnyAssociatedClient();
    }

    @Override
    public boolean canView(Long clientId) {
        return hasAssociatedClient(clientId);
    }

    private boolean hasAssociatedClient(Long clientId) {
        return loggedUserService.getCurrentEmployee().getAssociatedClientIds().contains(clientId);
    }

    private boolean hasAnyAssociatedClient() {
        return CollectionUtils.isNotEmpty(loggedUserService.getCurrentEmployee().getAssociatedClientIds());
    }
}
