package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.ResultObservation;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.ResultDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResultDetailsConverter implements Converter<ResultObservation, ResultDto> {
    @Override
    public ResultDto convert(ResultObservation resultObservation) {
        ResultDto resultDto = new ResultDto();
        resultDto.setId(resultObservation.getId());
        resultDto.setResultDate(resultObservation.getEffectiveTime().getTime());
        resultDto.setStatus(resultObservation.getStatusCode());
        resultDto.setType(resultObservation.getResultTypeCode().getDisplayName());
        resultDto.setValue(resultObservation.getValue() + ", " + resultObservation.getValueUnit());
        if (CollectionUtils.isNotEmpty(resultObservation.getInterpretationCodes())) {
            resultDto.setInterpretations(resultObservation.getInterpretationCodes().get(0).getDisplayName());
        }
        if (CollectionUtils.isNotEmpty(resultObservation.getReferenceRanges())) {
            String referenceRanges = StringUtils.join(resultObservation.getReferenceRanges(), "; ");
            resultDto.setReferenceRanges(referenceRanges);
        }
        resultDto.setDataSource(DataSourceService.transform(resultObservation.getDatabase(), resultObservation.getResult().getResident().getId()));
        return resultDto;
    }
}
