package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import org.springframework.stereotype.Component;

@Component
public class AdvancedDirectiveInfoPopulator implements Populator<AdvanceDirective, AdvanceDirectiveInfoDto> {

    @Override
    public void populate(AdvanceDirective src, AdvanceDirectiveInfoDto target) {
        target.setId(src.getId());
        target.setAdvanceDirectiveType(src.getTextType());
        if (src.getTimeHigh() != null) {
            target.setEffectiveTimeHigh(src.getTimeHigh().getTime());
        }
        if (src.getTimeLow() != null) {
            target.setEffectiveTimeLow(src.getTimeLow().getTime());
        }
        //result.setStatus(null);//TODO after DB status is available
    }
}
