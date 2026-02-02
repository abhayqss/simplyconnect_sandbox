package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResultObservation;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * @author phomal
 * Created on 4/26/2018.
 */
@Component
public class ResultObservationFactory {

    private final CcdCodeFactory ccdCodeFactory;
    private final AuthorFactory authorFactory;

    @Autowired
    public ResultObservationFactory(CcdCodeFactory ccdCodeFactory, AuthorFactory authorFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.authorFactory = authorFactory;
    }

    public ResultObservation parse(Observation ccdObservation, Resident resident, String LEGACY_TABLE) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final ResultObservation resultObservation = new ResultObservation();
        resultObservation.setDatabase(resident.getDatabase());

        final CD code = ccdObservation.getCode();
        resultObservation.setResultTypeCode(ccdCodeFactory.convert(code));
        resultObservation.setText(CcdTransform.EDtoString(code.getOriginalText(), resultObservation.getResultTypeCode()));

        if (CcdParseUtils.hasContent(ccdObservation.getStatusCode())) {
            resultObservation.setStatusCode(ccdObservation.getStatusCode().getCode());
        }

        // Effective time data type may be TS or IVL<TS>
        resultObservation.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime()));

        if (!CollectionUtils.isEmpty(ccdObservation.getMethodCodes())) {
            resultObservation.setMethodCode(ccdCodeFactory.convert(ccdObservation.getMethodCodes().get(0)));
        }
        if (!CollectionUtils.isEmpty(ccdObservation.getTargetSiteCodes())) {
            resultObservation.setTargetSiteCode(ccdCodeFactory.convert(ccdObservation.getTargetSiteCodes().get(0)));
        }
        if (!CollectionUtils.isEmpty(ccdObservation.getAuthors())) {
            resultObservation.setAuthor(authorFactory.parseAuthor(ccdObservation.getAuthors().get(0), resident, LEGACY_TABLE));
        }

        resultObservation.setInterpretationCodes(ccdCodeFactory.convertInterpretationCodes(ccdObservation));

        resultObservation.setReferenceRanges(ObservationFactory.parseReferenceRanges(ccdObservation.getReferenceRanges()));

        try {
            // Observation value data may be of ANY type?
            // TODO change value type to Double in order to store ST values as well
            PQ observationValue = ObservationFactory.getValue(ccdObservation, PQ.class);
            if (observationValue != null) {
                resultObservation.setValue(CcdTransform.PQtoInteger(observationValue));
                resultObservation.setValueUnit(observationValue.getUnit());
            }
        } catch (ClassCastException exc) {
            exc.printStackTrace();
        }

        return resultObservation;
    }


}
