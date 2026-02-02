package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Medication;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.hl7.fhir.instance.model.MedicationOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.*;
import static java.util.Optional.ofNullable;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class MedicationOrderToMedicationConverter implements ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<MedicationOrder, Medication> {

    @Autowired
    private ConsanaMedicationInformationConverter consanaMedicationInformationConverter;

    @Autowired
    private ConsanaMedicationDispenseConverter consanaMedicationDispenseConverter;

    @Override
    public Medication convert(MedicationOrder order, Resident resident) {
        Medication medication = new Medication();
        return convertInto(order, resident, medication);
    }

    @Override
    public Medication convertInto(MedicationOrder order, Resident resident, Medication target) {
        target.setLegacyId(0L);
        target.setDatabase(resident.getDatabase());
        target.setResident(resident);
        target.setPerson(resident.getPerson());
        target.setConsanaId(order.getId());
        target.setDoseQuantity(ofNullable(getDoseQuantity(order)).map(BigDecimal::intValue).orElse(null));
        target.setDoseUnits(getDoseUnit(order));
        target.setFreeTextSig(order.getNote());
        target.setMedicationStarted(getInstant(order.getDateWritten()));
        target.setMedicationStopped(getInstant(order.getDateEnded()));
        target.setRepeatNumber(ofNullable(order.getDispenseRequest()).map(MedicationOrder.MedicationOrderDispenseRequestComponent::getNumberOfRepeatsAllowed).orElse(null));
        target.setStatusCode(ofNullable(order.getStatus()).map(s -> s.toString().toLowerCase()).orElse(null));
        target.setMedicationInformation(consanaMedicationInformationConverter.convert(order, resident, target.getMedicationInformation()));
        target.setMedicationDispenses(consanaMedicationDispenseConverter.convert(order, resident, target.getMedicationDispenses()));
        return target;
    }
}
