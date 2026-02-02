package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.MedicationListItemDto;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MedicationListItemDtoConverter implements Converter<ClientMedication, MedicationListItemDto>, MedicationConverter {

    @Override
    public MedicationListItemDto convert(ClientMedication source) {
        MedicationListItemDto target = new MedicationListItemDto();
        fill(source, target);
        return target;
    }
}
