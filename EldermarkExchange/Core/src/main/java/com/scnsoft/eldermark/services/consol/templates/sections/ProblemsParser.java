package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.hl7.datatypes.ANY;
import org.openhealthtools.mdht.uml.cda.consol.ProblemConcernAct;
import org.openhealthtools.mdht.uml.cda.consol.ProblemSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Problems</h1> “This section lists and describes all relevant clinical
 * problems at the time the summary is generated. At a minimum, all pertinent
 * current and historical problems should be listed.” [CCD 3.5]
 *
 * @see Problem
 * @see ProblemObservation
 * @see Resident
 */
@Component("consol.ProblemsParser")
public class ProblemsParser extends AbstractParsableSection<ProblemConcernAct, ProblemSection, Problem>
        implements ParsableSection<ProblemSection, Problem> {

    private final ObservationFactory observationFactory;

    @Autowired
    public ProblemsParser(ObservationFactory observationFactory) {
        this.observationFactory = observationFactory;
    }

    @Override
    public boolean isEntryIgnored(final ProblemConcernAct entry) {
        final EList<org.openhealthtools.mdht.uml.cda.consol.ProblemObservation> problemObservations = entry
                .getProblemObservations();
        boolean result = true;
        for (org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservation : problemObservations) {
            final EList<ANY> values = problemObservation.getValues();
            for (ANY value : values) {
                if (value.getNullFlavor() == null || StringUtils.isEmpty(value.getNullFlavor().getName())) {
                    return false;
                }
                if (!"UNK".equals(value.getNullFlavor().getName())) {
                    return false;
                } else {
                    result = true;

                }
            }
        }
        return result;
    }

    @Override
    public List<Problem> doParseSection(Resident resident, ProblemSection problemSection) {
        if (!CcdParseUtils.hasContent(problemSection)
                || CollectionUtils.isEmpty(problemSection.getConsolProblemConcerns())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Problem> targetList = new ArrayList<>();
        for (ProblemConcernAct srcAct : problemSection.getConsolProblemConcerns()) {
            if (isEntryIgnored(srcAct)) {
                continue;
            }
            final Problem targetProblem = new Problem();
            targetProblem.setProblemObservations(new ArrayList<ProblemObservation>());
            targetList.add(targetProblem);

            if (CcdParseUtils.hasContent(srcAct.getStatusCode())) {
                targetProblem.setStatusCode(srcAct.getStatusCode().getCode());
            }

            final Pair<Date, Date> actEffectiveTime = CcdTransform
                    .IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(srcAct.getEffectiveTime());
            if (actEffectiveTime != null) {
                targetProblem.setTimeHigh(actEffectiveTime.getFirst());
                targetProblem.setTimeLow(actEffectiveTime.getSecond());
            }

            // TODO test on real examples
            for (org.openhealthtools.mdht.uml.cda.consol.ProblemObservation srcObservation : srcAct
                    .getProblemObservations()) {
                final ProblemObservation targetObservation = observationFactory.parseProblemObservation(srcObservation,
                        resident, targetProblem);
                if (filterProblemObservation(targetObservation)) {
                    targetProblem.getProblemObservations().add(targetObservation);
                }
            }
            targetProblem.setResident(resident);
            // targetProblem.setRank(?);
            targetProblem.setDatabase(resident.getDatabase());
            targetProblem.setDatabaseId(resident.getDatabaseId());
            targetProblem.setLegacyId(CcdParseUtils.getFirstIdExtension(srcAct.getIds()));
        }
        return targetList;
    }

    private boolean filterProblemObservation(ProblemObservation problemObservation) {
        return (problemObservation != null && StringUtils.isNotEmpty(problemObservation.getProblemName()));
    }
}
