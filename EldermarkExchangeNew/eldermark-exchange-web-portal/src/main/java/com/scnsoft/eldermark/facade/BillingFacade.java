package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.client.BillingInfoDto;

public interface BillingFacade {

    BillingInfoDto findByClientId(Long clientId);
}
