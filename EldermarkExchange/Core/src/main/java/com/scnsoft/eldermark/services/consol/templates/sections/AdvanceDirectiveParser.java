package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectivesSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Advance Directives</h1> “This section contains data defining the
 * patient’s advance directives and any reference to supporting documentation...
 * This section contains data such as the existence of living wills, healthcare
 * proxies, and CPR and resuscitation status.” [CCD 3.2]
 *
 * @see AdvanceDirective
 * @see AdvanceDirectiveDocument
 * @see Organization
 * @see Name
 * @see Participant
 * @see Person
 * @see Resident
 */
@Component("consol.AdvanceDirectiveParser")
public class AdvanceDirectiveParser
        extends AbstractParsableSection<InfrastructureRoot, AdvanceDirectivesSection, AdvanceDirective>
        implements ParsableSection<AdvanceDirectivesSection, AdvanceDirective> {

    private static final Logger logger = LoggerFactory.getLogger(AdvanceDirectiveParser.class);
    private static final String LEGACY_TABLE = "AdvanceDirective_NWHIN";

    private final ObservationFactory observationFactory;

    @Autowired
    public AdvanceDirectiveParser(ObservationFactory observationFactory) {
        this.observationFactory = observationFactory;
    }

    @Override
    public List<AdvanceDirective> doParseSection(final Resident resident,
            final AdvanceDirectivesSection advanceDirectivesSection) {
        if (!CcdParseUtils.hasContent(advanceDirectivesSection)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<AdvanceDirective> advanceDirectives = Lists.transform(
                advanceDirectivesSection.getConsolAdvanceDirectiveObservations(),
                new Function<Observation, AdvanceDirective>() {
                    @Override
                    public AdvanceDirective apply(Observation input) {
                        return observationFactory.parseAdvanceDirective(input, resident, LEGACY_TABLE);
                    }
                });
        return filterAdvanceDirectivesList(advanceDirectives);
    }

    private List<AdvanceDirective> filterAdvanceDirectivesList(List<AdvanceDirective> advanceDirectives) {
        return FluentIterable.from(advanceDirectives).filter(new Predicate<AdvanceDirective>() {
            @Override
            public boolean apply(AdvanceDirective advanceDirective) {
                return advanceDirective != null && advanceDirective.getType() != null
                        && StringUtils.isNotEmpty(advanceDirective.getType().getDisplayName());
            }
        }).toList();
    }
}
