package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Allergy;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.AllergyIntolerance;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaAllergyIntoleranceToAllergyConverter {

    public Allergy convert(AllergyIntolerance source, Resident resident, Allergy target) {
        if (target == null) {
            target = new Allergy();
        }
        target.setLegacyId(0L);
        target.setTimeLow(FhirConversionUtils.getInstant(source.getOnset()));
        target.setDatabase(resident.getDatabase());
        target.setOrganization(resident.getFacility());
        target.setResident(resident);
        return target;
    }
}
