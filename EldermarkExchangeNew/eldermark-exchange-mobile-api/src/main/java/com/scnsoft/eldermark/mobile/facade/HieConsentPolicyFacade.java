package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.ClientHieConsentPolicyDto;

import java.util.List;

public interface HieConsentPolicyFacade {

    void update(ClientHieConsentPolicyDto dto);

    List<ClientHieConsentPolicyDto> findAll();

    boolean canView();
}
