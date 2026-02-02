package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.document.ccd.AdvanceDirective;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectivesSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
 * @see Client
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
    public boolean isSectionIgnored(AdvanceDirectivesSection section) {
        return !CcdParseUtils.hasContent(section);
    }

    @Override
    public List<AdvanceDirective> doParseSection(Client client,
                                                 AdvanceDirectivesSection advanceDirectivesSection) {
        Objects.requireNonNull(client);

        var advanceDirectives = advanceDirectivesSection.getConsolAdvanceDirectiveObservations()
                .stream()
                .map(input -> observationFactory.parseAdvanceDirective(input, client, LEGACY_TABLE))
                .filter(this::filterAdvanceDirective)
                .collect(Collectors.toList());
        return advanceDirectives;
    }

    private boolean filterAdvanceDirective(AdvanceDirective advanceDirective) {
        return advanceDirective != null && advanceDirective.getType() != null
                && StringUtils.isNotEmpty(advanceDirective.getType().getDisplayName());
    }
}
