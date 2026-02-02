package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.InsuranceNetworkDto;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.InsurancePlan;

@Component
public class InsuranceNetworkDtoListConverter implements ListAndItemConverter<InNetworkInsurance, InsuranceNetworkDto> {

    @Autowired
    ListAndItemConverter<InsurancePlan, InsurancePlanDto> insurancePlanDtoConverter;

    @Override
    public InsuranceNetworkDto convert(InNetworkInsurance source) {
        InsuranceNetworkDto insuranceNetworkDto = new InsuranceNetworkDto();
        insuranceNetworkDto.setId(source.getId());
        insuranceNetworkDto.setTitle(source.getDisplayName());
        insuranceNetworkDto.setPopular(source.getPopular());
        insuranceNetworkDto.setName(source.getKey());
        insuranceNetworkDto.setPaymentPlans(insurancePlanDtoConverter.convertList(source.getInsurancePlans()));
        return insuranceNetworkDto;
    }
}