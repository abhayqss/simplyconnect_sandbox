package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.MedicationDispense;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.MedicationOrder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.MED_DISPENSE_LEGACY_TABLE;
import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.getDispenseQuantity;
import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.getEffectiveDateRange;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaMedicationDispenseConverter {

    public List<MedicationDispense> convert(MedicationOrder source, Resident resident, List<MedicationDispense> target) {
        if (!source.hasDispenseRequest()) {
            if (target != null) {
                target.clear();
            }
            return target;
        }
        //medications from consana should have only one dispense
        var medicationDispense = FhirConversionUtils.getOrCreateCollectionElement(target, 0, MedicationDispense::new);
        var dispense = source.getDispenseRequest();
        medicationDispense.setLegacyId(0L);
        medicationDispense.setLegacyTable(MED_DISPENSE_LEGACY_TABLE);
        var effectiveDateRange = getEffectiveDateRange(dispense);
        medicationDispense.setEffectiveTimeLow(effectiveDateRange.getVal1());
        medicationDispense.setEffectiveTimeHigh(effectiveDateRange.getVal2());
        medicationDispense.setRepeatNumber(dispense.getNumberOfRepeatsAllowed());
        medicationDispense.setQuantity(getDispenseQuantity(dispense));
        medicationDispense.setDatabase(resident.getDatabase());
        medicationDispense.setOrganization(resident.getFacility());
        return FhirConversionUtils.updateCollection(List.of(medicationDispense), target);
    }
}