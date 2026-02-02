package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.ANY;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public VitalSignObservation parse(Observation ccdObservation, Client resident, String LEGACY_TABLE) {
        final VitalSignObservation vitalSignObservation = new VitalSignObservation();
        vitalSignObservation.setOrganization(resident.getOrganization());
        vitalSignObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdObservation.getIds()));

        vitalSignObservation.setResultTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        final IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
        vitalSignObservation.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(observationEffectiveTime));

        // TODO refactoring: extract to a utility method
        if (CollectionUtils.isNotEmpty(ccdObservation.getValues())) {
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
