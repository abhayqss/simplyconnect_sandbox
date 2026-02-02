package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.web.entity.FamilyHistoryObservationListItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FamilyHistoryObservationListItemDtoConverter implements Converter<FamilyHistoryObservation, FamilyHistoryObservationListItemDto> {

    @Override
    public FamilyHistoryObservationListItemDto convert(FamilyHistoryObservation familyHistoryObservation) {
        FamilyHistoryObservationListItemDto dto = new FamilyHistoryObservationListItemDto();
        dto.setId(familyHistoryObservation.getId());
        dto.setName(familyHistoryObservation.getProblemValue() != null ? familyHistoryObservation.getProblemValue().getDisplayName() : null);
        dto.setDate(familyHistoryObservation.getEffectiveTime());
        return dto;
    }
}
