package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class IndicationFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public IndicationFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public Indication parseIndication(Observation ccdObservation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || client == null) {
            return null;
        }

        Indication indication = new Indication();
        indication.setOrganization(client.getOrganization());
        // TODO: inbound ID type is String
        indication.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));
        indication.setLegacyTable(legacyTable);
        indication.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        Pair<Date, Date> highLowTime = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (highLowTime != null) {
            indication.setTimeHigh(highLowTime.getFirst());
            indication.setTimeLow(highLowTime.getSecond());
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getValues()) && ccdObservation.getValues().get(0) instanceof CD) {
            indication.setValue(ccdCodeFactory.convert((CD) ccdObservation.getValues().get(0)));
        }

        return indication;
    }

}
