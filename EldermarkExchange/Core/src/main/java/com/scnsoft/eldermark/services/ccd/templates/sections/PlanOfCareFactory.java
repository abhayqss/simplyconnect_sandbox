package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.services.cda.HTMLSanitizerService;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Encounter;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
@Component
public class PlanOfCareFactory extends RequiredTemplateFactory
        implements ParsableSectionFactory<PlanOfCareSection, PlanOfCare> {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    @Autowired
    private HTMLSanitizerService htmlSanitizerService;

    @Override
    public PlanOfCareSection buildTemplateInstance(Collection<PlanOfCare> planOfCares) {
        final PlanOfCareSection planOfCareSection = CCDFactory.eINSTANCE.createPlanOfCareSection();
        planOfCareSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.10"));

        planOfCareSection.setCode(CcdUtils.createCE("18776-5", "Plan of Care", CodeSystem.LOINC));

        planOfCareSection.setTitle(DatatypesFactory.eINSTANCE.createST("Plan Of Care"));

        planOfCareSection.createStrucDocText(buildSectionText(planOfCares));

        if (!CollectionUtils.isEmpty(planOfCares)) {
            for (PlanOfCare planOfCare : planOfCares) {

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityActList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityActList()) {

                        PlanOfCareActivityAct planOfCareActivityAct = CCDFactory.eINSTANCE
                                .createPlanOfCareActivityAct();
                        planOfCareSection.addAct(planOfCareActivityAct);
                        planOfCareActivityAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
                        planOfCareActivityAct
                                .setMoodCode(x_DocumentActMood.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivityAct.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.39"));
                        planOfCareActivityAct.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));

                        if (planOfCareActivity.getCode() != null) {
                            planOfCareActivityAct.setCode(
                                    CcdUtils.createCD(planOfCareActivity.getCode(), CodeSystem.SNOMED_CT.getOid()));
                        }
                        if (planOfCareActivity.getEffectiveTime() != null) {
                            planOfCareActivityAct
                                    .setEffectiveTime(CcdUtils.createCenterTime(planOfCareActivity.getEffectiveTime()));
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityEncounterList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityEncounterList()) {

                        PlanOfCareActivityEncounter planOfCareActivityEncounter = CCDFactory.eINSTANCE
                                .createPlanOfCareActivityEncounter();
                        planOfCareSection.addEncounter(planOfCareActivityEncounter);
                        planOfCareActivityEncounter.setClassCode(ActClass.ENC);
                        planOfCareActivityEncounter
                                .setMoodCode(x_DocumentEncounterMood.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivityEncounter.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.40"));
                        planOfCareActivityEncounter.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityObservationList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityObservationList()) {

                        PlanOfCareActivityObservation planOfCareActivityObservation = CCDFactory.eINSTANCE
                                .createPlanOfCareActivityObservation();
                        planOfCareSection.addObservation(planOfCareActivityObservation);
                        planOfCareActivityObservation.setClassCode(ActClassObservation.OBS);
                        planOfCareActivityObservation
                                .setMoodCode(x_ActMoodDocumentObservation.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivityObservation.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.44"));
                        planOfCareActivityObservation.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));

                        if (planOfCareActivity.getCode() != null) {
                            planOfCareActivityObservation.setCode(
                                    CcdUtils.createCD(planOfCareActivity.getCode(), CodeSystem.SNOMED_CT.getOid()));
                        }
                        if (planOfCareActivity.getEffectiveTime() != null) {
                            planOfCareActivityObservation
                                    .setEffectiveTime(CcdUtils.createCenterTime(planOfCareActivity.getEffectiveTime()));
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityProcedureList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityProcedureList()) {

                        PlanOfCareActivityProcedure planOfCareActivityProcedure = CCDFactory.eINSTANCE
                                .createPlanOfCareActivityProcedure();
                        planOfCareSection.addProcedure(planOfCareActivityProcedure);
                        planOfCareActivityProcedure.setClassCode(ActClass.PROC);
                        planOfCareActivityProcedure
                                .setMoodCode(x_DocumentProcedureMood.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivityProcedure.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.41"));
                        planOfCareActivityProcedure.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));

                        if (planOfCareActivity.getCode() != null) {
                            planOfCareActivityProcedure.setCode(
                                    CcdUtils.createCD(planOfCareActivity.getCode(), CodeSystem.SNOMED_CT.getOid()));
                        }
                        if (planOfCareActivity.getEffectiveTime() != null) {
                            planOfCareActivityProcedure
                                    .setEffectiveTime(CcdUtils.createCenterTime(planOfCareActivity.getEffectiveTime()));
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivitySubstanceAdministrationList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare
                            .getPlanOfCareActivitySubstanceAdministrationList()) {

                        PlanOfCareActivitySubstanceAdministration planOfCareActivitySubstanceAdministration = CCDFactory.eINSTANCE
                                .createPlanOfCareActivitySubstanceAdministration();
                        planOfCareSection.addSubstanceAdministration(planOfCareActivitySubstanceAdministration);
                        planOfCareActivitySubstanceAdministration.setClassCode(ActClass.SBADM);
                        planOfCareActivitySubstanceAdministration
                                .setMoodCode(x_DocumentSubstanceMood.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivitySubstanceAdministration.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.42"));
                        planOfCareActivitySubstanceAdministration.getIds()
                                .add(CcdUtils.getId(planOfCareActivity.getId()));

                        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
                        planOfCareActivitySubstanceAdministration.setConsumable(consumable);
                        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
                        consumable.setManufacturedProduct(manufacturedProduct);
                        LabeledDrug labeledDrug = CDAFactory.eINSTANCE.createLabeledDrug();
                        manufacturedProduct.setManufacturedLabeledDrug(labeledDrug);

                        labeledDrug.setCode(
                                CcdUtils.createCE(planOfCareActivity.getCode(), CodeSystem.SNOMED_CT.getOid()));
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivitySupplyList())) {

                    for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivitySupplyList()) {

                        PlanOfCareActivitySupply planOfCareActivitySupply = CCDFactory.eINSTANCE
                                .createPlanOfCareActivitySupply();
                        planOfCareSection.addSupply(planOfCareActivitySupply);
                        planOfCareActivitySupply.setClassCode(ActClassSupply.SPLY);
                        planOfCareActivitySupply
                                .setMoodCode(x_DocumentSubstanceMood.getByName(planOfCareActivity.getMoodCode()));
                        planOfCareActivitySupply.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.43"));
                        planOfCareActivitySupply.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));
                    }
                }

                if (!CollectionUtils.isEmpty(planOfCare.getInstructions())) {

                    for (Instructions instruction : planOfCare.getInstructions()) {
                        Act instructionAct = SectionEntryFactory.buildInstructions(instruction,
                                new HashSet<Class>(Arrays.asList(Instructions.class))); // TODO to ask
                        planOfCareSection.addAct(instructionAct);
                    }

                }
            }
        }

        return planOfCareSection;
    }

    private static String buildSectionText(Collection<PlanOfCare> planOfCares) {

        if (CollectionUtils.isEmpty(planOfCares)) {
            return "No plan of care.";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Planned Activity</th>");
        sectionText.append("<th>Planned Date</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        List<PlanOfCareActivity> addedActivities = new ArrayList<>();

        for (PlanOfCare planOfCare : planOfCares) {
            if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityActList())) {
                for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityActList()) {
                    addSection(planOfCareActivity, sectionText, addedActivities);
                }
            }

            if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityObservationList())) {
                for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityObservationList()) {
                    addSection(planOfCareActivity, sectionText, addedActivities);
                }
            }

            if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityProcedureList())) {
                for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityProcedureList()) {
                    addSection(planOfCareActivity, sectionText, addedActivities);
                }
            }
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

    private static void addSection(PlanOfCareActivity planOfCareActivity, StringBuilder sectionText,
            List<PlanOfCareActivity> addedActivities) {
        if ((planOfCareActivity.getCode() != null
                && StringUtils.isNotBlank(planOfCareActivity.getCode().getDisplayName()))
                && !checkAdded(planOfCareActivity, addedActivities)) {
            sectionText.append("<tr>");
            CcdUtils.addCellToSectionText(planOfCareActivity.getCode(), sectionText);
            CcdUtils.addDateCell(planOfCareActivity.getEffectiveTime(), sectionText);
            addedActivities.add(planOfCareActivity);
            sectionText.append("</tr>");
        }
    }

    // TODO Should we need this method?
    private static boolean checkAdded(PlanOfCareActivity planOfCareActivity, List<PlanOfCareActivity> addedActivities) {
        for (PlanOfCareActivity addedActivity : addedActivities) {
            if (addedActivity.getCode().equals(planOfCareActivity.getCode())) {
                if (addedActivity.getEffectiveTime() != null) {
                    return addedActivity.getEffectiveTime().equals(planOfCareActivity.getEffectiveTime());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<PlanOfCare> parseSection(Resident resident, PlanOfCareSection planOfCareSection) {
        if (!CcdParseUtils.hasContent(planOfCareSection)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final PlanOfCare planOfCare = new PlanOfCare();
        planOfCare.setDatabase(resident.getDatabase());
        planOfCare.setResident(resident);

        if (!CollectionUtils.isEmpty(planOfCareSection.getActs())) {
            List<PlanOfCareActivity> planOfCareActivityActs = new ArrayList<>();
            List<Instructions> instructions = new ArrayList<>();
            for (Act act : planOfCareSection.getActs()) {
                if (!CcdParseUtils.hasContent(act)) {
                    continue;
                }
                if (act.getMoodCode() == x_DocumentActMood.INT) {
                    // Instructions
                    Instructions instruction = sectionEntryParseFactory.parseInstructions(act, resident);
                    instructions.add(instruction);
                } else {
                    // Plan of Care Activity Act
                    PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                    planOfCareActivity.setDatabase(resident.getDatabase());

                    planOfCareActivity.setMoodCode(act.getMoodCode().getName());
                    planOfCareActivity.setCode(ccdCodeFactory.convert(act.getCode()));
                    planOfCareActivity.setEffectiveTime(CcdParseUtils.parseCenterTime(act.getEffectiveTime()));

                    planOfCareActivityActs.add(planOfCareActivity);
                }
            }
            planOfCare.setPlanOfCareActivityActList(planOfCareActivityActs);
            planOfCare.setInstructions(instructions);
        }

        if (!CollectionUtils.isEmpty(planOfCareSection.getEncounters())) {
            List<PlanOfCareActivity> planOfCareActivityEncounters = new ArrayList<>();
            for (Encounter encounter : planOfCareSection.getEncounters()) {
                if (!CcdParseUtils.hasContent(encounter)) {
                    continue;
                }
                PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                planOfCareActivity.setDatabase(resident.getDatabase());
                planOfCareActivity.setMoodCode(encounter.getMoodCode().getName());

                planOfCareActivityEncounters.add(planOfCareActivity);
            }
            planOfCare.setPlanOfCareActivityEncounterList(planOfCareActivityEncounters);
        }

        if (!CollectionUtils.isEmpty(planOfCareSection.getObservations())) {
            List<PlanOfCareActivity> planOfCareActivityObservations = new ArrayList<>();
            for (Observation observation : planOfCareSection.getObservations()) {
                if (!CcdParseUtils.hasContent(observation)) {
                    continue;
                }
                PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                planOfCareActivity.setDatabase(resident.getDatabase());

                planOfCareActivity.setMoodCode(observation.getMoodCode().getName());
                planOfCareActivity.setCode(ccdCodeFactory.convert(observation.getCode()));
                planOfCareActivity.setEffectiveTime(CcdParseUtils.parseCenterTime(observation.getEffectiveTime()));
                if (planOfCareActivity.getEffectiveTime() == null) {
                    Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(observation.getEffectiveTime());
                    if (effectiveTime != null) {
                        planOfCareActivity.setEffectiveTime(effectiveTime.getSecond());
                    }
                }

                planOfCareActivityObservations.add(planOfCareActivity);
            }
            planOfCare.setPlanOfCareActivityObservationList(planOfCareActivityObservations);
        }

        if (!CollectionUtils.isEmpty(planOfCareSection.getProcedures())) {
            List<PlanOfCareActivity> planOfCareActivityProcedures = new ArrayList<>();
            for (Procedure procedure : planOfCareSection.getProcedures()) {
                if (!CcdParseUtils.hasContent(procedure)) {
                    continue;
                }
                PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                planOfCareActivity.setDatabase(resident.getDatabase());

                planOfCareActivity.setMoodCode(procedure.getMoodCode().getName());
                planOfCareActivity.setCode(ccdCodeFactory.convert(procedure.getCode()));
                planOfCareActivity.setEffectiveTime(CcdParseUtils.parseCenterTime(procedure.getEffectiveTime()));

                planOfCareActivityProcedures.add(planOfCareActivity);
            }
            planOfCare.setPlanOfCareActivityProcedureList(planOfCareActivityProcedures);
        }

        if (!CollectionUtils.isEmpty(planOfCareSection.getSubstanceAdministrations())) {
            List<PlanOfCareActivity> planOfCareActivitySubstanceAdministrations = new ArrayList<>();
            for (SubstanceAdministration substanceAdministration : planOfCareSection.getSubstanceAdministrations()) {
                if (!CcdParseUtils.hasContent(substanceAdministration)) {
                    continue;
                }
                PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                planOfCareActivity.setDatabase(resident.getDatabase());
                planOfCareActivity.setMoodCode(substanceAdministration.getMoodCode().getName());

                if (substanceAdministration.getConsumable() != null) {
                    ManufacturedProduct manufacturedProduct = substanceAdministration.getConsumable()
                            .getManufacturedProduct();
                    if (manufacturedProduct != null) {
                        LabeledDrug manufacturedLabeledDrug = manufacturedProduct.getManufacturedLabeledDrug();
                        if (manufacturedLabeledDrug != null) {
                            planOfCareActivity.setCode(ccdCodeFactory.convert(manufacturedLabeledDrug.getCode()));
                        }
                    }
                }

                planOfCareActivitySubstanceAdministrations.add(planOfCareActivity);
            }
            planOfCare.setPlanOfCareActivitySubstanceAdministrationList(planOfCareActivitySubstanceAdministrations);
        }

        if (!CollectionUtils.isEmpty(planOfCareSection.getSupplies())) {
            List<PlanOfCareActivity> planOfCareActivitySupplies = new ArrayList<>();
            for (Supply supply : planOfCareSection.getSupplies()) {
                if (!CcdParseUtils.hasContent(supply)) {
                    continue;
                }
                PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
                planOfCareActivity.setDatabase(resident.getDatabase());
                planOfCareActivity.setMoodCode(supply.getMoodCode().getName());

                planOfCareActivitySupplies.add(planOfCareActivity);
            }
            planOfCare.setPlanOfCareActivitySupplyList(planOfCareActivitySupplies);
        }

        final String freeText = htmlSanitizerService
                .sanitizeCdaNarrativeBlock(CcdParseUtils.parseFreeText(planOfCareSection));
        planOfCare.setFreeText(freeText);

        return Collections.singletonList(planOfCare);
    }

}