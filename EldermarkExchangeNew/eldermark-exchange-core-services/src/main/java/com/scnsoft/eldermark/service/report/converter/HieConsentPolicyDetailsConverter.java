package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.HieConsentPolicyDetailsAware;
import com.scnsoft.eldermark.entity.client.report.HieConsentPolicyDetailsItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class HieConsentPolicyDetailsConverter implements Converter<HieConsentPolicyDetailsAware, HieConsentPolicyDetailsItem> {
    @Override
    public HieConsentPolicyDetailsItem convert(HieConsentPolicyDetailsAware detailsAware) {
        return new HieConsentPolicyDetailsItem(
                detailsAware.getOrganizationId(),
                detailsAware.getCommunityId(),
                detailsAware.getCommunityName(),
                detailsAware.getId(),
                detailsAware.getActive(),
                detailsAware.getFullName(),
                detailsAware.getHieConsentPolicyType(),
                detailsAware.getHieConsentPolicyObtainedFrom(),
                detailsAware.getHieConsentPolicyObtainedBy(),
                detailsAware.getHieConsentPolicySource(),
                detailsAware.getHieConsentPolicyUpdateDateTime(),
                detailsAware.getLastUpdated());
    }
}
