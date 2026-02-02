package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.PlanOfCare;
import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import java.util.*;

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
@Component("consol.PlanOfCareFactory")
public class PlanOfCareFactory extends RequiredTemplateFactory
        implements SectionFactory<PlanOfCareSection, PlanOfCare> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.10";

    @Override
    public PlanOfCareSection buildTemplateInstance(Collection<PlanOfCare> planOfCares) {
        final PlanOfCareSection planOfCareSection = ConsolFactory.eINSTANCE.createPlanOfCareSection();
        planOfCareSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        planOfCareSection.setCode(CcdUtils.createCE("18776-5", "Plan of Care", CodeSystem.LOINC));

        planOfCareSection.setTitle(DatatypesFactory.eINSTANCE.createST("Plan Of Care"));

        planOfCareSection.createStrucDocText(buildSectionText(planOfCares));

        if (CollectionUtils.isEmpty(planOfCares)) {
            // TODO buildNullPlanOfCareSection ?
            return planOfCareSection;
        }

        for (PlanOfCare planOfCare : planOfCares) {

            if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivityActList())) {

                for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivityActList()) {

                    PlanOfCareActivityAct planOfCareActivityAct = ConsolFactory.eINSTANCE.createPlanOfCareActivityAct();
                    planOfCareSection.addAct(planOfCareActivityAct);
                    planOfCareActivityAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
                    planOfCareActivityAct.setMoodCode(x_DocumentActMood.getByName(planOfCareActivity.getMoodCode()));
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

                    PlanOfCareActivityEncounter planOfCareActivityEncounter = ConsolFactory.eINSTANCE
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

                    PlanOfCareActivityObservation planOfCareActivityObservation = ConsolFactory.eINSTANCE
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

                    PlanOfCareActivityProcedure planOfCareActivityProcedure = ConsolFactory.eINSTANCE
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

                    PlanOfCareActivitySubstanceAdministration planOfCareActivitySubstanceAdministration = ConsolFactory.eINSTANCE
                            .createPlanOfCareActivitySubstanceAdministration();
                    planOfCareSection.addSubstanceAdministration(planOfCareActivitySubstanceAdministration);
                    planOfCareActivitySubstanceAdministration.setClassCode(ActClass.SBADM);
                    planOfCareActivitySubstanceAdministration
                            .setMoodCode(x_DocumentSubstanceMood.getByName(planOfCareActivity.getMoodCode()));
                    planOfCareActivitySubstanceAdministration.getTemplateIds()
                            .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.42"));
                    planOfCareActivitySubstanceAdministration.getIds().add(CcdUtils.getId(planOfCareActivity.getId()));

                    Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
                    planOfCareActivitySubstanceAdministration.setConsumable(consumable);
                    ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
                    consumable.setManufacturedProduct(manufacturedProduct);
                    LabeledDrug labeledDrug = CDAFactory.eINSTANCE.createLabeledDrug();
                    manufacturedProduct.setManufacturedLabeledDrug(labeledDrug);

                    labeledDrug.setCode(CcdUtils.createCE(planOfCareActivity.getCode(), CodeSystem.SNOMED_CT.getOid()));
                }
            }

            if (!CollectionUtils.isEmpty(planOfCare.getPlanOfCareActivitySupplyList())) {

                for (PlanOfCareActivity planOfCareActivity : planOfCare.getPlanOfCareActivitySupplyList()) {

                    PlanOfCareActivitySupply planOfCareActivitySupply = ConsolFactory.eINSTANCE
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
}