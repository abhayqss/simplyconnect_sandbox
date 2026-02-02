package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Encounter;
import com.scnsoft.eldermark.consana.sync.server.model.entity.EncounterPerformer;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaEncounterToEncounterConverter implements ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<org.hl7.fhir.instance.model.Encounter, Encounter> {

    @Autowired
    private ConsanaEncounterToDeliveryLocationConverter deliveryLocationConverter;

    @Autowired
    private ConsanaEncounterToIndicationConverter indicationConverter;

    @Autowired
    private ConsanaEncounterToPersonConverter personConverter;

    @Override
    public Encounter convert(org.hl7.fhir.instance.model.Encounter source, Resident resident) {
        var target = new Encounter();
        return convertInto(source, resident, target);
    }

    @Override
    public Encounter convertInto(org.hl7.fhir.instance.model.Encounter source, Resident resident, Encounter target) {
        target.setLegacyId(0L);
        target.setEffectiveTime(source.hasPeriod() ? FhirConversionUtils.getInstant(source.getPeriod().getStart()) : null);
        target.setDatabase(resident.getDatabase());
        target.setResident(resident);
        target.setEncounterTypeText(convertEncounterTypeText(source));
        target.setDeliveryLocations(deliveryLocationConverter.convert(source, resident, target.getDeliveryLocations()));
        target.setIndications(indicationConverter.convert(source, resident, target.getIndications()));
        target.setEncounterPerformers(convertEncounterPerformers(source, resident, target));
        target.setProblemObservation(null);
        target.setConsanaId(source.getId());
        return target;
    }

    private String convertEncounterTypeText(org.hl7.fhir.instance.model.Encounter source) {
        if (!source.hasType()) {
            return null;
        }

        return Stream.ofNullable(source.getType())
                .flatMap(List::stream)
                .map(CodeableConcept::getCoding)
                .flatMap(List::stream)
                .filter(Coding::hasDisplay)
                .map(Coding::getDisplay)
                .findFirst()
                .orElse(null);
    }

    private List<EncounterPerformer> convertEncounterPerformers(org.hl7.fhir.instance.model.Encounter source, Resident resident, Encounter encounter) {
        var target = FhirConversionUtils.getOrCreateCollectionElement(encounter.getEncounterPerformers(), 0, EncounterPerformer::new);
        target.setDatabase(resident.getDatabase());
        target.setPerformer(personConverter.convert(source, resident, target.getPerformer()));
        target.setEncounter(encounter);
        return FhirConversionUtils.updateCollection(List.of(target), encounter.getEncounterPerformers());
    }
}
