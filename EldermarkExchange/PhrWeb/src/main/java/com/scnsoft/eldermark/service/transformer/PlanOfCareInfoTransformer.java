package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.PlanOfCareDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlanOfCareInfoTransformer extends BasePhrService implements Converter<PlanOfCareActivity, PlanOfCareDto> {

    @Override
    public PlanOfCareDto convert(PlanOfCareActivity planOfCareActivity) {
        final PlanOfCareDto destination = new PlanOfCareDto();
        destination.setId(planOfCareActivity.getId());
        destination.setPlannedActivity(planOfCareActivity.getCode() != null ? planOfCareActivity.getCode().getDisplayName() : null);
        destination.setActivityDate(planOfCareActivity.getEffectiveTime() != null ? planOfCareActivity.getEffectiveTime().getTime() : null);
        destination.setDataSource(DataSourceService.transform(planOfCareActivity.getDatabase(), null));
        return destination;
    }
}