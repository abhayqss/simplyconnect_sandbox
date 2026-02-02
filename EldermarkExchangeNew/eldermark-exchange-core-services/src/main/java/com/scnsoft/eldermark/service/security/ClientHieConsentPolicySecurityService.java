package com.scnsoft.eldermark.service.security;

public interface ClientHieConsentPolicySecurityService {

    boolean canEdit(Long clientId);

    boolean canView();

    boolean canView(Long clientId);
}
