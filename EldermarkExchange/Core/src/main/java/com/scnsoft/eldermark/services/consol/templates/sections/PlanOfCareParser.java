package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.PlanOfCare;
import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.HTMLSanitizerService;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.InstructionsFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PlanOfCareActivityFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.openhealthtools.mdht.uml.cda.consol.PlanOfCareSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public List<PlanOfCare> doParseSection(Resident resident, PlanOfCareSection planOfCareSection) {
        if (!CcdParseUtils.hasContent(planOfCareSection)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final PlanOfCare planOfCare = new PlanOfCare();
        planOfCare.setDatabase(resident.getDatabase());
        planOfCare.setResident(resident);

        final List<Instructions> instructions = instructionsFactory.parseInstructions(resident,
                planOfCareSection.getInstructionss());
        final List<PlanOfCareActivity> actList = planOfCareActivityFactory.parseActs(resident,
                planOfCareSection.getPlanOfCareActivityActs());
        final List<PlanOfCareActivity> encounterList = planOfCareActivityFactory.parseEncounters(resident,
                planOfCareSection.getEncounters());
        final List<PlanOfCareActivity> observationList = planOfCareActivityFactory.parseObservations(resident,
                planOfCareSection.getObservations());
        final List<PlanOfCareActivity> procedureList = planOfCareActivityFactory.parseProcedures(resident,
                planOfCareSection.getProcedures());
        final List<PlanOfCareActivity> substanceAdministrationList = planOfCareActivityFactory
                .parseSubstanceAdministrations(resident, planOfCareSection.getSubstanceAdministrations());
        final List<PlanOfCareActivity> supplyList = planOfCareActivityFactory.parseSupplies(resident,
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
        return FluentIterable.from(activityLists).filter(new Predicate<PlanOfCareActivity>() {
            @Override
            public boolean apply(PlanOfCareActivity planOfCareActivity) {
                return planOfCareActivity.getCode() != null
                        && StringUtils.isNotEmpty(planOfCareActivity.getCode().getDisplayName());
            }
        }).toList();
    }
}
