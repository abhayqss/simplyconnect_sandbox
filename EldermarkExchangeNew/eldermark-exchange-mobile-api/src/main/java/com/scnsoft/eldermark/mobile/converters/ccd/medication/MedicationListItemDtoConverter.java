package com.scnsoft.eldermark.mobile.converters.ccd.medication;

import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationListItemDto;
import com.scnsoft.eldermark.service.security.ClientMedicationSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MedicationListItemDtoConverter implements Converter<ClientMedication, MedicationListItemDto>,
        BaseMedicationDtoConverter {

    @Autowired
    private ClientMedicationSecurityService clientMedicationSecurityService;

    @Override
    public MedicationListItemDto convert(ClientMedication source) {
        var target = new MedicationListItemDto();
        fill(source, target);
        target.setCanEdit(clientMedicationSecurityService.canEdit(source));
        return target;
    }
}
