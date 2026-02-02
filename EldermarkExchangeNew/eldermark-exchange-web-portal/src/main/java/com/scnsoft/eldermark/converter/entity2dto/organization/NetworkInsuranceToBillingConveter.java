package com.scnsoft.eldermark.converter.entity2dto.organization;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.client.BillingItemDto;
import com.scnsoft.eldermark.entity.Client;

@Component
public class NetworkInsuranceToBillingConveter implements Converter<Client, BillingItemDto> {

    @Override
    public BillingItemDto convert(Client source) {
        BillingItemDto target = new BillingItemDto();
        String network = source.getInNetworkInsurance() != null ? source.getInNetworkInsurance().getDisplayName()
                : null;
        String plan = source.getInsurancePlan();
        target.setGroupNumber(source.getGroupNumber());
        target.setInsurance(network);
        target.setInsurancePlanConcat(Stream.of(plan, network)
                .filter(StringUtils::isNoneEmpty).collect(Collectors.joining(" ")));
        target.setPlan(plan);
        target.setPolicyNumber(source.getMemberNumber());
        return target;
    }

}
