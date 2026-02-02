package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.ANY;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author phomal
 * Created on 4/26/2018.
 */
@Component
public class VitalSignObservationFactory {

    private final CcdCodeFactory ccdCodeFactory;
    private final AuthorFactory authorFactory;

    @Autowired
    public VitalSignObservationFactory(CcdCodeFactory ccdCodeFactory, AuthorFactory authorFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.authorFactory = authorFactory;
    }

    public VitalSignObservation parse(Observation ccdObservation, Resident resident, String LEGACY_TABLE) {
        final VitalSignObservation vitalSignObservation = new VitalSignObservation();
        vitalSignObservation.setDatabase(resident.getDatabase());
        vitalSignObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdObservation.getIds()));

        vitalSignObservation.setResultTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        final IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
        vitalSignObservation.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(observationEffectiveTime));

        // TODO refactoring: extract to a utility method
        if (!CollectionUtils.isEmpty(ccdObservation.getValues())) {
            // TODO is it possible to have multiple observation values?
            final ANY any = ccdObservation.getValues().get(0);
            if (any instanceof PQ) {
                final PQ pq = (PQ)any;
                vitalSignObservation.setUnit(pq.getUnit());
                vitalSignObservation.setValue(pq.getValue() != null ? pq.getValue().doubleValue() : null);
            }
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getInterpretationCodes())) {
            vitalSignObservation.setInterpretationCode(ccdCodeFactory.convert(ccdObservation.getInterpretationCodes().get(0)));
        }
        if (!CollectionUtils.isEmpty(ccdObservation.getMethodCodes())) {
            vitalSignObservation.setMethodCode(ccdCodeFactory.convert(ccdObservation.getMethodCodes().get(0)));
        }
        if (!CollectionUtils.isEmpty(ccdObservation.getTargetSiteCodes())) {
            vitalSignObservation.setTargetSiteCode(ccdCodeFactory.convert(ccdObservation.getTargetSiteCodes().get(0)));
        }
        if (!CollectionUtils.isEmpty(ccdObservation.getAuthors())) {
            vitalSignObservation.setAuthor(authorFactory.parseAuthor(ccdObservation.getAuthors().get(0), resident, LEGACY_TABLE));
        }

        return vitalSignObservation;
    }

}
