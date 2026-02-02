package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.web.entity.FamilyHistoryObservationInfoDto;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FamilyHistoryObservationInfoDtoConverter implements Converter<FamilyHistoryObservation, FamilyHistoryObservationInfoDto> {

    @Override
    public FamilyHistoryObservationInfoDto convert(FamilyHistoryObservation familyHistoryObservation) {
        final FamilyHistoryObservationInfoDto dto = new FamilyHistoryObservationInfoDto();
        dto.setProblemType(familyHistoryObservation.getProblemTypeCode() != null ? familyHistoryObservation.getProblemTypeCode().getDisplayName() : null);
        dto.setProblemName(familyHistoryObservation.getProblemValue() != null ? familyHistoryObservation.getProblemValue().getDisplayName() : null);
        dto.setDate(familyHistoryObservation.getEffectiveTime());

        //for currently supported CCD:
        //SHALL contain exactly one [1..1] statusCode="completed" Completed
        //(CodeSystem: ActStatus 2.16.840.1.113883.5.14) (CONF:8602)
        //to be done: add column and parse actual status value
        dto.setStatus("Completed");

        if (familyHistoryObservation.getEffectiveTime() != null && familyHistoryObservation.getFamilyHistory().getBirthTime() != null) {
            final int years = Years.yearsBetween(new DateTime(familyHistoryObservation.getEffectiveTime()),
                    new DateTime(familyHistoryObservation.getFamilyHistory().getBirthTime())).getYears();
            dto.setObservedAge(years + (years == 1 ? " year": " years"));
        }
        dto.setIsObservedDead(familyHistoryObservation.getDeceased());
        return dto;
    }
}
