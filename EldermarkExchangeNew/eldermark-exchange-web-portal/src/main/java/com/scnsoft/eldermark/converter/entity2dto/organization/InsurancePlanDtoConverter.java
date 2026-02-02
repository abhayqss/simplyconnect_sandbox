package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.InsurancePlanDto;
import com.scnsoft.eldermark.entity.InsurancePlan;
import org.springframework.stereotype.Component;

@Component
public class InsurancePlanDtoConverter implements ListAndItemConverter<InsurancePlan, InsurancePlanDto> {
    @Override
    public InsurancePlanDto convert(InsurancePlan source) {
        InsurancePlanDto target = new InsurancePlanDto();
        target.setId(source.getId());
        target.setTitle(source.getDisplayName());
        target.setName(source.getKey());
        target.setPopular(source.getPopular());
        return target;
    }
}
