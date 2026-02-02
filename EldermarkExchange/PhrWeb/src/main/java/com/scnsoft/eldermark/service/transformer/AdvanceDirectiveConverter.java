package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveDto;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AdvanceDirectiveConverter implements Converter<AdvanceDirective, AdvanceDirectiveDto> {

    @Autowired
    private Populator<AdvanceDirective, AdvanceDirectiveInfoDto> advancedDirectiveInfoPopulator;

    @Autowired
    private Populator<AdvanceDirective, AdvanceDirectiveDto> advancedDirectivePopulator;

    @Override
    public AdvanceDirectiveDto convert(AdvanceDirective src) {
        if (src == null) {
            return null;
        }
        final AdvanceDirectiveDto result = new AdvanceDirectiveDto();
        advancedDirectiveInfoPopulator.populate(src, result);
        advancedDirectivePopulator.populate(src, result);
        return result;
    }
}
