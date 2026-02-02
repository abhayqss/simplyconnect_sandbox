package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ResultObservation;
import com.scnsoft.eldermark.web.entity.ResultInfoDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResultListConverter implements Converter<ResultObservation, ResultInfoDto> {
    @Override
    public ResultInfoDto convert(ResultObservation result) {
        final ResultInfoDto resultInfoDto = new ResultInfoDto();
        resultInfoDto.setId(result.getId());
        resultInfoDto.setResultDate(result.getEffectiveTime().getTime());
        resultInfoDto.setType(result.getResultTypeCode().getDisplayName());
        return resultInfoDto;
    }
}
