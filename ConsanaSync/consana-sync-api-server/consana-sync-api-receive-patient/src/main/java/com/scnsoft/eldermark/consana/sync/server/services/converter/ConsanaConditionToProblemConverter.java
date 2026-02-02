package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Problem;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.Condition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaConditionToProblemConverter {

    public Problem convert(Condition source, Resident resident, Problem target) {
        if (target == null) {
            target = new Problem();
        }
        target.setLegacyId(0L);
        target.setTimeLow(source.hasOnsetDateTimeType() ? FhirConversionUtils.getInstant(source.getOnsetDateTimeType().getValue()) : null);
        target.setTimeHigh(source.hasAbatementDateTimeType() ? FhirConversionUtils.getInstant(source.getAbatementDateTimeType().getValue()) : null);
        target.setDatabase(resident.getDatabase());
        target.setResident(resident);
        return target;
    }
}
