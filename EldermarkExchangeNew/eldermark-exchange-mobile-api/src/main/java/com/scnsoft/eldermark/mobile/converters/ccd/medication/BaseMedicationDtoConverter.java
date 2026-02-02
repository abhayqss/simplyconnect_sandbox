package com.scnsoft.eldermark.mobile.converters.ccd.medication;

import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.BaseMedicationDto;
import com.scnsoft.eldermark.util.DateTimeUtils;

import java.util.Optional;

public interface BaseMedicationDtoConverter {

    default void fill(ClientMedication source, BaseMedicationDto target) {
        target.setId(source.getId());
        target.setName(Optional.ofNullable(source.getMedicationInformation()).map(MedicationInformation::getProductNameText).orElse(null));
        target.setStartedDate(DateTimeUtils.toEpochMilli(source.getMedicationStarted()));
        target.setStoppedDate(DateTimeUtils.toEpochMilli(source.getMedicationStopped()));
        target.setPrnScheduled(source.getPrnScheduled());
    }
}
