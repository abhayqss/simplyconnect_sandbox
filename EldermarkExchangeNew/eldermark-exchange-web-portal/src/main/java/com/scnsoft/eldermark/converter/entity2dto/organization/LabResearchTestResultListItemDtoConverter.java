package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.lab.LabResearchTestResultListItemDto;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderObservationResult;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class LabResearchTestResultListItemDtoConverter implements Converter<LabResearchOrderObservationResult, LabResearchTestResultListItemDto> {
    @Override
    public LabResearchTestResultListItemDto convert(LabResearchOrderObservationResult source) {
        var result = new LabResearchTestResultListItemDto();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setValue(source.getValue());
        result.setUnits(source.getUnitsText());
        result.setRefRange(source.getLimits());
        result.setAbnormalFlags(source.getAbnormalFlags());
        return result;
    }
}
