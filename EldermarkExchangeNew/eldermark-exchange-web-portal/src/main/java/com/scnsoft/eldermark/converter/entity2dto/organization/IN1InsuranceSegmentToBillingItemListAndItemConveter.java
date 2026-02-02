package com.scnsoft.eldermark.converter.entity2dto.organization;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.client.BillingItemDto;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;

@Component
public class IN1InsuranceSegmentToBillingItemListAndItemConveter
        implements ListAndItemConverter<IN1InsuranceSegment, BillingItemDto> {

    @Override
    public BillingItemDto convert(IN1InsuranceSegment source) {
        BillingItemDto target = new BillingItemDto();        
        String network = source.getInsuranceCompanyName() != null ? source.getInsuranceCompanyName().getOrganizationName() : null;
        String plan = source.getInsurancePlanId() != null ? source.getInsurancePlanId().getText() : null;       
        target.setGroupNumber(source.getGroupNumber());
        target.setInsurance(network);
        target.setInsurancePlanConcat(Stream.of(plan, network).filter(StringUtils::isNoneEmpty)
                .collect(Collectors.joining(" ")));        
        target.setPlan(plan);
        target.setPolicyNumber(null);
        return target;
    }

}
