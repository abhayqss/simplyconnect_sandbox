package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.AllergyObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Allergies, Adverse reactions</h1> “This section is used to list and
 * describe any allergies, adverse reactions, and alerts that are pertinent to
 * the patient’s current or past medical history.” [CCD 3.8]
 *
 * @see Allergy
 */
@Component("consol.AllergiesParser")
public class AllergiesParser extends AbstractParsableSection<AllergyProblemAct, AllergiesSection, Allergy>
        implements ParsableSection<AllergiesSection, Allergy> {

    private static final Logger logger = LoggerFactory.getLogger(AllergiesParser.class);
    private static final String LEGACY_TABLE = "Allergy_NWHIN";

    private final AllergyObservationFactory allergyObservationFactory;

    @Autowired
    public AllergiesParser(AllergyObservationFactory allergyObservationFactory) {
        this.allergyObservationFactory = allergyObservationFactory;
    }

    @Override
    public boolean isSectionIgnored(AllergiesSection section) {
        return !CcdParseUtils.hasContent(section)
                || CollectionUtils.isEmpty(section.getConsolAllergyProblemActs());
    }

    @Override
    public List<Allergy> doParseSection(Client resident, AllergiesSection allergiesSection) {
        checkNotNull(resident);

        final List<Allergy> allergies = new ArrayList<>();
        for (AllergyProblemAct ccdProblemAct : allergiesSection.getConsolAllergyProblemActs()) {
            if (isEntryIgnored(ccdProblemAct)) {
                continue;
            }
            final Allergy allergy = new Allergy();
            allergy.setClient(resident);
            allergy.setOrganization(resident.getOrganization());
            allergy.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProblemAct.getIds()));

            final CD statusCode = ccdProblemAct.getStatusCode();
            allergy.setStatusCode(statusCode != null ? statusCode.getCode() : null);

            final Pair<Date, Date> effectiveTime = CcdTransform
                    .IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdProblemAct.getEffectiveTime());
            if (effectiveTime != null) {
                allergy.setTimeHigh(effectiveTime.getFirst());
                allergy.setTimeLow(effectiveTime.getSecond());
            }

            // TODO replace ccdProblemAct.getEntryRelationships() with
            // ccdProblemAct.getAllergyObservations()
            final Set<AllergyObservation> allergyObservations = allergyObservationFactory
                    .parse(ccdProblemAct.getEntryRelationships(), allergy, resident, LEGACY_TABLE);
            allergy.setAllergyObservations(filterAllergyObservation(allergyObservations));
            allergies.add(allergy);
        }
        return allergies;
    }

    private Set<AllergyObservation> filterAllergyObservation(Set<AllergyObservation> allergyObservations) {
        return allergyObservations.stream()
                .filter(allergyObservation -> StringUtils.isNotEmpty(allergyObservation.getProductText()))
                .collect(Collectors.toSet());
    }
}
