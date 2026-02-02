package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.MedicationListItemDto;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.util.DateTimeUtils;

import java.util.Optional;

public interface MedicationConverter {

    default void fill(ClientMedication source, MedicationListItemDto target) {
        target.setId(source.getId());
        target.setName(Optional.ofNullable(source.getMedicationInformation()).map(MedicationInformation::getProductNameText).orElse(null));
        target.setDirections(source.getFreeTextSig());
        target.setStartedDate(DateTimeUtils.toEpochMilli(source.getMedicationStarted()));
        target.setStoppedDate(DateTimeUtils.toEpochMilli(source.getMedicationStopped()));
    }
}
