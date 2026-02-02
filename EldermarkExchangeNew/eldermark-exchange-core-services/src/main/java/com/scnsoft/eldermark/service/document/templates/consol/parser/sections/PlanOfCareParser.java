package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCareActivity;
import com.scnsoft.eldermark.service.document.cda.HTMLSanitizerService;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.InstructionsFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PlanOfCareActivityFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.openhealthtools.mdht.uml.cda.consol.PlanOfCareSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1>Plan of Treatment (formerly Plan of Care)</h1> This section contains “All
 * active, incomplete, or pending orders, appointments, referrals, procedures,
 * services, or any other pending event of clinical significance to the current
 * and ongoing care of the patient ... The plan of care section also contains
 * information regarding clinical reminders, patient’s values, beliefs,
 * preferences, care expectations, and overarching care goals.” [CCD 3.16]
 *
 * @see PlanOfCare
 * @see PlanOfCareActivity
 * @see Instructions
 */
@Component("consol.PlanOfCareParser")
public class PlanOfCareParser extends AbstractParsableSection<InfrastructureRoot, PlanOfCareSection, PlanOfCare>
        implements ParsableSection<PlanOfCareSection, PlanOfCare> {

    private final InstructionsFactory instructionsFactory;
    private final PlanOfCareActivityFactory planOfCareActivityFactory;
    private final HTMLSanitizerService htmlSanitizerService;

    @Autowired
    public PlanOfCareParser(InstructionsFactory instructionsFactory,
            PlanOfCareActivityFactory planOfCareActivityFactory, HTMLSanitizerService htmlSanitizerService) {
        this.instructionsFactory = instructionsFactory;
        this.planOfCareActivityFactory = planOfCareActivityFactory;
        this.htmlSanitizerService = htmlSanitizerService;
    }

    @Override
    public boolean isSectionIgnored(PlanOfCareSection planOfCareSection) {
        return !CcdParseUtils.hasContent(planOfCareSection);
    }

    @Override
    public List<PlanOfCare> doParseSection(Client resident, PlanOfCareSection planOfCareSection) {
        Objects.requireNonNull(resident);

        final PlanOfCare planOfCare = new PlanOfCare();
        planOfCare.setOrganization(resident.getOrganization());
        planOfCare.setClient(resident);

        var instructions = instructionsFactory.parseInstructions(resident,
                planOfCareSection.getInstructionss());
        var actList = planOfCareActivityFactory.parseActs(resident,
                planOfCareSection.getPlanOfCareActivityActs());
        var encounterList = planOfCareActivityFactory.parseEncounters(resident,
                planOfCareSection.getEncounters());
        var observationList = planOfCareActivityFactory.parseObservations(resident,
                planOfCareSection.getObservations());
        var procedureList = planOfCareActivityFactory.parseProcedures(resident,
                planOfCareSection.getProcedures());
        var substanceAdministrationList = planOfCareActivityFactory
                .parseSubstanceAdministrations(resident, planOfCareSection.getSubstanceAdministrations());
        var supplyList = planOfCareActivityFactory.parseSupplies(resident,
                planOfCareSection.getSupplies());
        final String freeText = htmlSanitizerService
                .sanitizeCdaNarrativeBlock(CcdParseUtils.parseFreeText(planOfCareSection));

        planOfCare.setInstructions(instructions);

        // columns
        if (actList != null) {
            planOfCare.setPlanOfCareActivityActList(filterPlanOfCareActivityLists(actList));
        }

        if (observationList != null) {
            planOfCare.setPlanOfCareActivityObservationList(filterPlanOfCareActivityLists(observationList));
        }

        if (procedureList != null) {
            planOfCare.setPlanOfCareActivityProcedureList(filterPlanOfCareActivityLists(procedureList));
        }

        planOfCare.setPlanOfCareActivityEncounterList(encounterList);
        planOfCare.setPlanOfCareActivitySubstanceAdministrationList(substanceAdministrationList);
        planOfCare.setPlanOfCareActivitySupplyList(supplyList);
        planOfCare.setFreeText(freeText);
        return Collections.singletonList(planOfCare);
    }

    private List<PlanOfCareActivity> filterPlanOfCareActivityLists(List<PlanOfCareActivity> activityLists) {
        return activityLists.stream()
                .filter(planOfCareActivity -> planOfCareActivity.getCode() != null && StringUtils.isNotEmpty(planOfCareActivity.getCode().getDisplayName()))
                .collect(Collectors.toList());
    }
}
