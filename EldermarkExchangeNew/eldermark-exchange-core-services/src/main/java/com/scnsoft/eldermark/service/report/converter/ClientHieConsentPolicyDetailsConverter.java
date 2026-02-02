package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientHieConsentPolicyDetailsAware;
import com.scnsoft.eldermark.entity.client.report.HieConsentPolicyDetailsItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientHieConsentPolicyDetailsConverter implements Converter<ClientHieConsentPolicyDetailsAware, HieConsentPolicyDetailsItem> {
    @Override
    public HieConsentPolicyDetailsItem convert(ClientHieConsentPolicyDetailsAware detailsAware) {
        return new HieConsentPolicyDetailsItem(
                detailsAware.getOrganizationId(),
                detailsAware.getCommunityId(),
                detailsAware.getCommunityName(),
                detailsAware.getClientId(),
                detailsAware.getActive(),
                detailsAware.getClientFullName(),
                detailsAware.getHieConsentPolicyType(),
                detailsAware.getHieConsentPolicyObtainedFrom(),
                detailsAware.getHieConsentPolicyObtainedBy(),
                detailsAware.getHieConsentPolicySource(),
                detailsAware.getHieConsentPolicyUpdateDateTime(),
                detailsAware.getLastUpdated()
        );
    }
}
