package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.entity.SeverityObservation;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.services.CcdCodeService;
import org.hl7.fhir.instance.model.AllergyIntolerance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaAllergyIntoleranceToSeverityObservationConverter {

    @Autowired
    private CcdCodeService ccdCodeService;

    public SeverityObservation convert(AllergyIntolerance source, Resident resident, SeverityObservation target) {
        var severityCode = convertSeverityCode(source.getCriticality());
        if (severityCode == null) {
            return null;
        }
        if (target == null) {
            target = new SeverityObservation();
        }
        target.setSeverityCode(severityCode);
        target.setSeverityText(target.getSeverityCode() != null ? target.getSeverityCode().getDisplayName() : null);
        target.setLegacyId(0L);
        target.setLegacyTable("Allergy_NWHIN");
        target.setDatabase(resident.getDatabase());
        return target;
    }

    private CcdCode convertSeverityCode(AllergyIntolerance.AllergyIntoleranceCriticality criticality) {
        if (AllergyIntolerance.AllergyIntoleranceCriticality.CRITL == criticality) {
            return ccdCodeService.findByCodeAndCodeSystem("255604002", CodeSystem.SNOMED_CT.getOid());
        }
        if (AllergyIntolerance.AllergyIntoleranceCriticality.CRITH == criticality) {
            return ccdCodeService.findByCodeAndCodeSystem("24484000", CodeSystem.SNOMED_CT.getOid());
        }
        return null;
    }
}
