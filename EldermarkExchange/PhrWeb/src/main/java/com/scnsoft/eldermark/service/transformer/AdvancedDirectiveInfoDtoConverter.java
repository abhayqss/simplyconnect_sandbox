package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AdvancedDirectiveInfoDtoConverter implements Converter<AdvanceDirective, AdvanceDirectiveInfoDto> {

    @Autowired
    private Populator<AdvanceDirective, AdvanceDirectiveInfoDto> advancedDirectiveInfoPopulator;

    @Override
    public AdvanceDirectiveInfoDto convert(AdvanceDirective src) {
        final AdvanceDirectiveInfoDto result = new AdvanceDirectiveInfoDto();
        advancedDirectiveInfoPopulator.populate(src, result);
        return result;
    }
}
