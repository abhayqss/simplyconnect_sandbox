package com.scnsoft.eldermark.converter.entity2dto.organization;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.client.BillingItemDto;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;

@Component
public class ClientHealthPlanToBillingListItemConverter
        implements ListAndItemConverter<ClientHealthPlan, BillingItemDto> {

    @Override
    public BillingItemDto convert(ClientHealthPlan source) {
        String plan = source.getClient().getInsurancePlan();
        BillingItemDto target = new BillingItemDto();
        target.setGroupNumber(source.getGroupNumber());
        target.setInsurance(source.getHealthPlanName());
        target.setInsurancePlanConcat(Stream.of(plan, source.getHealthPlanName()).filter(StringUtils::isNoneEmpty)
                .collect(Collectors.joining(" ")));        
        target.setPlan(plan);
        target.setPolicyNumber(source.getPolicyNumber());
        return target;
    }

}
