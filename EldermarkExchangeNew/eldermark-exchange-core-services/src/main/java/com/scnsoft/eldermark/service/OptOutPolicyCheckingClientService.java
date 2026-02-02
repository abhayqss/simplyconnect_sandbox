package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.HieConsentPolicyTypeAware;

public interface OptOutPolicyCheckingClientService {

    boolean isOptOutPolicy(HieConsentPolicyTypeAware client);

}
