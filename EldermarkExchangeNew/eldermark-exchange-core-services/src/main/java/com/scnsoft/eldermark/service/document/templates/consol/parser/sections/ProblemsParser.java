package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.hl7.datatypes.ANY;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.cda.consol.ProblemConcernAct;
import org.openhealthtools.mdht.uml.cda.consol.ProblemSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * <h1>Problems</h1> “This section lists and describes all relevant clinical
 * problems at the time the summary is generated. At a minimum, all pertinent
 * current and historical problems should be listed.” [CCD 3.5]
 *
 * @see Problem
 * @see ProblemObservation
 * @see com.scnsoft.eldermark.entity.Client
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
        var problemObservations = entry.getProblemObservations();

        for (var ccdProblemObservation : problemObservations) {
            final EList<ANY> values = ccdProblemObservation.getValues();
            for (ANY value : values) {
                if (!NullFlavor.UNK.equals(value.getNullFlavor())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isSectionIgnored(ProblemSection problemSection) {
        return !CcdParseUtils.hasContent(problemSection)
                || CollectionUtils.isEmpty(problemSection.getConsolProblemConcerns());
    }

    @Override
    public List<Problem> doParseSection(Client resident, ProblemSection problemSection) {
        Objects.requireNonNull(resident);

        final List<Problem> targetList = new ArrayList<>();
        for (ProblemConcernAct srcAct : problemSection.getConsolProblemConcerns()) {
            if (isEntryIgnored(srcAct)) {
                continue;
            }
            final Problem targetProblem = new Problem();
            targetProblem.setProblemObservations(new ArrayList<>());
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
            for (var srcObservation : srcAct.getProblemObservations()) {
                var targetObservation = observationFactory.parseProblemObservation(srcObservation, resident, targetProblem);
                if (filterProblemObservation(targetObservation)) {
                    targetProblem.getProblemObservations().add(targetObservation);
                }
            }
            targetProblem.setClient(resident);
            // targetProblem.setRank(?);
            targetProblem.setOrganization(resident.getOrganization());
            targetProblem.setOrganizationId(resident.getOrganizationId());
            targetProblem.setLegacyId(CcdParseUtils.getFirstIdExtension(srcAct.getIds()));
        }
        return targetList;
    }

    private boolean filterProblemObservation(ProblemObservation problemObservation) {
        return (problemObservation != null && StringUtils.isNotEmpty(problemObservation.getProblemName()));
    }
}
