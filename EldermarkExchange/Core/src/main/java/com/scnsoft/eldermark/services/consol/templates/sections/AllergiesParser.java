package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.Allergy;
import com.scnsoft.eldermark.entity.AllergyObservation;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.AllergyObservationFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
    public List<Allergy> doParseSection(Resident resident, AllergiesSection allergiesSection) {
        if (!CcdParseUtils.hasContent(allergiesSection)
                || CollectionUtils.isEmpty(allergiesSection.getConsolAllergyProblemActs())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Allergy> allergies = new ArrayList<>();
        for (AllergyProblemAct ccdProblemAct : allergiesSection.getConsolAllergyProblemActs()) {
            if (isEntryIgnored(ccdProblemAct)) {
                continue;
            }
            final Allergy allergy = new Allergy();
            allergy.setResident(resident);
            allergy.setDatabase(resident.getDatabase());
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
        return FluentIterable.from(allergyObservations).filter(new Predicate<AllergyObservation>() {
            @Override
            public boolean apply(AllergyObservation allergyObservation) {
                return StringUtils.isNotEmpty(allergyObservation.getProductText());
            }
        }).toSet();
    }
}
