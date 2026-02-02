package com.scnsoft.eldermark.service.security;

public interface PartnerNetworkSecurityService {

    boolean canView(Long id);

    boolean canViewList();
}
