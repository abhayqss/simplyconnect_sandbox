package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.web.entity.FamilyHistoryListItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FamilyHistoryListItemDtoConverter implements Converter<FamilyHistory, FamilyHistoryListItemDto> {

    @Override
    public FamilyHistoryListItemDto convert(FamilyHistory familyHistoryObservation) {
        FamilyHistoryListItemDto familyHistoryListItemDto = new FamilyHistoryListItemDto();
        familyHistoryListItemDto.setId(familyHistoryObservation.getId());
        familyHistoryListItemDto.setName(familyHistoryObservation.getRelatedSubjectCode() != null ? familyHistoryObservation.getRelatedSubjectCode().getDisplayName() : null);
        return familyHistoryListItemDto;
    }
}
