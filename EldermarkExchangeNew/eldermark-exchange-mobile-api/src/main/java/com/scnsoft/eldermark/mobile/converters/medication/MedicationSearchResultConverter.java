package com.scnsoft.eldermark.mobile.converters.medication;

import com.scnsoft.eldermark.mobile.dto.medication.MedicationSearchResultDto;
import com.scnsoft.eldermark.service.medispan.dto.MedicationSearchResult;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MedicationSearchResultConverter implements Converter<MedicationSearchResult, MedicationSearchResultDto> {

    @Override
    public MedicationSearchResultDto convert(MedicationSearchResult source) {
        var dto = new MedicationSearchResultDto();
        dto.setMediSpanId(source.getMediSpanId());
        dto.setName(source.getName());
        dto.setDosageForm(source.getDoseForm());
        dto.setRoute(source.getRoute());
        dto.setStrength(source.getStrength());
        dto.setNdcCodes(source.getNdcCodes());
        return dto;
    }
}
