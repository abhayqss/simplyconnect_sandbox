package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.DeliveryLocation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Reference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaEncounterToDeliveryLocationConverter {

    public List<DeliveryLocation> convert(Encounter source, Resident resident, List<DeliveryLocation> target) {
        if (!source.hasLocation()) {
            if (target != null) {
                target.clear();
            }
            return target;
        }

        var deliveryLocations = new ArrayList<DeliveryLocation>();
        source.getLocation().stream()
                .filter(Encounter.EncounterLocationComponent::hasLocation)
                .map(Encounter.EncounterLocationComponent::getLocation)
                .filter(Reference::hasDisplay)
                .map(Reference::getDisplay)
                .forEach(d -> {
                    DeliveryLocation deliveryLocation = FhirConversionUtils.getOrCreateCollectionElement(target, deliveryLocations.size(), DeliveryLocation::new);
                    deliveryLocation.setName(d);
                    deliveryLocation.setDatabase(resident.getDatabase());
                    deliveryLocations.add(deliveryLocation);
                });

        return FhirConversionUtils.updateCollection(deliveryLocations, target);
    }
}
