package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.web.entity.PlanOfCareInfoDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlanOfCareTransformer implements Converter<PlanOfCareActivity, PlanOfCareInfoDto> {

    @Override
    public PlanOfCareInfoDto convert(PlanOfCareActivity src) {
        if (src == null) {
            return null;
        }
        PlanOfCareInfoDto destination = new PlanOfCareInfoDto();
        destination.setId(src.getId());
        destination.setPlannedActivity(src.getCode() != null ? src.getCode().getDisplayName() : null);
        destination.setActivityDate(src.getEffectiveTime() != null ? src.getEffectiveTime().getTime() : null);
        return destination;
    }
}