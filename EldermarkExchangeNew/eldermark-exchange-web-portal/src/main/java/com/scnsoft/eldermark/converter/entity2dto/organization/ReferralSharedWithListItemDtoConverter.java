package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.referral.ReferralSharedWithListItemDto;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReferralSharedWithListItemDtoConverter implements ItemConverter<ReferralRequest, ReferralSharedWithListItemDto> {

    @Override
    public ReferralSharedWithListItemDto convert(ReferralRequest source) {
        ReferralSharedWithListItemDto target = new ReferralSharedWithListItemDto();
        convert(source, target);
        return target;
    }

    @Override
    public void convert(ReferralRequest source, ReferralSharedWithListItemDto target) {
        target.setId(source.getId());
        target.setCommunity(source.getCommunity().getName());
        target.setOrganization(source.getCommunity().getOrganization().getName());

        var networkNames = CollectionUtils.emptyIfNull(source.getPartnerNetworks()).stream()
                .map(PartnerNetwork::getName)
                .collect(Collectors.joining(", "));
        target.setNetwork(StringUtils.defaultString(networkNames, null));

        if (source.getLastResponse() != null) {
            target.setStatusName(source.getLastResponse().getResponse().name());
            target.setStatusTitle(source.getLastResponse().getResponse().getValue());
            target.setDate(source.getLastResponse().getResponseDatetime().toEpochMilli());
        } else {
            target.setStatusName(ReferralStatus.PENDING.name());
            target.setStatusTitle(ReferralStatus.PENDING.getDisplayName());
            target.setDate(source.getReferral().getRequestDatetime().toEpochMilli());
        }
    }
}
