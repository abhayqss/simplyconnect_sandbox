package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <h1>Problems</h1>
 * “This section lists and describes all relevant clinical problems at the time the summary is generated.
 * At a minimum, all pertinent current and historical problems should be listed.” [CCD 3.5]
 *
 * @see Problem
 * @see ProblemObservation
 * @see Resident
 */
@Component("consol.ProblemsFactory")
public class ProblemsFactory extends RequiredTemplateFactory implements SectionFactory<ProblemSection, Problem> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.5.1";

    @Override
    public ProblemSection buildTemplateInstance(Collection<Problem> problems) {
        final ProblemSection problemSection = ConsolFactory.eINSTANCE.createProblemSection();
        problemSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        CE sectionCode = CcdUtils.createCE("11450-4", "Problem List", CodeSystem.LOINC);
        problemSection.setCode(sectionCode);

        ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Problems");
        problemSection.setTitle(title);

        problemSection.createStrucDocText(buildSectionText(problems));

        if (!CollectionUtils.isEmpty(problems)) {
            for (Problem problem : problems) {
                Entry entry = CDAFactory.eINSTANCE.createEntry();
                entry.setAct(buildProblemAct(problem));
                problemSection.getEntries().add(entry);
            }
        } else {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setAct(buildNullProblemAct());
            problemSection.getEntries().add(entry);
        }

        return problemSection;
    }

    private ProblemConcernAct buildProblemAct(Problem problem) {
        final ProblemConcernAct problemAct = ConsolFactory.eINSTANCE.createProblemConcernAct();
        problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
        problemAct.setMoodCode(x_DocumentActMood.EVN);

        II problemActTemplateId = DatatypesFactory.eINSTANCE.createII();
        problemActTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.3");
        problemAct.getTemplateIds().add(problemActTemplateId);

        problemAct.getIds().add(CcdUtils.getId(problem.getId()));

        CD problemActCode = CcdUtils.createCD("CONC", "Concern", CodeSystem.HL7_ACT_CLASS);
        problemAct.setCode(problemActCode);

        CS problemActStatusCode;
        ProblemActStatusCode actStatusCode = ProblemActStatusCode.getByCode(problem.getStatusCode());
        if (actStatusCode == null) {
            problemActStatusCode = CcdUtils.createCS(problem.getStatusCode());
        } else {
            problemActStatusCode = CcdUtils.createCS(actStatusCode);
        }
        problemAct.setStatusCode(problemActStatusCode);

        if (problem.getTimeLow() != null || problem.getTimeHigh() != null) {
            problemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(problem.getTimeLow(), problem.getTimeHigh()));
        } else {
            problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        }

        List<ProblemObservation> problemObservations = problem.getProblemObservations();
        if (!CollectionUtils.isEmpty(problemObservations)) {
            for (ProblemObservation problemObservation : problemObservations) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);

                Set<Class> entriesReferredToSectionText = new HashSet<>();
                entriesReferredToSectionText.add(ProblemObservation.class);

                entryRelationship.setObservation(buildProblemObservation(problemObservation, entriesReferredToSectionText));
                problemAct.getEntryRelationships().add(entryRelationship);
            }
        } else {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setObservation(SectionEntryFactory.buildNullProblemObservation());
            problemAct.getEntryRelationships().add(entryRelationship);
        }

        return problemAct;
    }

    private ProblemConcernAct buildNullProblemAct() {
        final ProblemConcernAct problemAct = ConsolFactory.eINSTANCE.createProblemConcernAct();
        problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
        problemAct.setMoodCode(x_DocumentActMood.EVN);

        II problemActTemplateId = DatatypesFactory.eINSTANCE.createII();
        problemActTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.3");
        problemAct.getTemplateIds().add(problemActTemplateId);

        problemAct.getIds().add(CcdUtils.getNullId());

        CD problemActCode = CcdUtils.createCD("CONC", "Concern", CodeSystem.HL7_ACT_CLASS);
        problemAct.setCode(problemActCode);

        CS problemStatusCode = DatatypesFactory.eINSTANCE.createCS();
        problemStatusCode.setNullFlavor(NullFlavor.NI);
        problemAct.setStatusCode(problemStatusCode);

        problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        entryRelationship.setObservation(SectionEntryFactory.buildNullProblemObservation());
        problemAct.getEntryRelationships().add(entryRelationship);

        return problemAct;
    }

    private static String buildSectionText(Collection<Problem> problems) {
        StringBuilder sectionText = new StringBuilder();

        if (CollectionUtils.isEmpty(problems)) {
            return "No known problems.";
        }

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Problem Name</th>");
        sectionText.append("<th>Dates</th>");
        sectionText.append("<th>Status</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Problem problem : problems) {
            if (problem.getProblemObservations() != null) {
                sectionText.append("<tr>");
                for (ProblemObservation problemObservation : problem.getProblemObservations()) {
                    sectionText.append("<td>");
                    if (problemObservation.getProblemName() != null) {
                        CcdUtils.addReferenceToSectionText(ProblemObservation.class.getSimpleName() + problemObservation.getId(), problemObservation.getProblemName(), sectionText);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("<td>");
                    if (problemObservation.getProblemDateTimeLow() != null || problemObservation.getProblemDateTimeHigh() != null) {
                        CcdUtils.addDateRangeToSectionText(problemObservation.getProblemDateTimeLow(), problemObservation.getProblemDateTimeHigh(), sectionText);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("<td>");
                    if (problemObservation.getProblemStatusText() != null) {
                        CcdUtils.addReferenceToSectionText(ProblemObservation.class.getSimpleName() + "Status" + problemObservation.getId(), problemObservation.getProblemStatusText(), sectionText);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");
                }
                sectionText.append("</tr>");
            }
        }
        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

    // TODO refactoring: DRY, move to shared utils
    public static Observation buildProblemObservation(ProblemObservation problemObservation, Set<Class> entriesReferredToSectionText) {
        org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservationCcd = ConsolFactory.eINSTANCE.createProblemObservation();

        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.4");
        problemObservationCcd.getTemplateIds().add(alertObservationTemplateId);

        problemObservationCcd.getIds().add(CcdUtils.getId(problemObservation.getId()));
        CcdUtils.addConsanaId(problemObservationCcd.getIds(), problemObservation.getConsanaId());

        if (problemObservation.getNegationInd() != null) {
            problemObservationCcd.setNegationInd(problemObservation.getNegationInd());
        }

        problemObservationCcd.setCode(CcdUtils.createCD(problemObservation.getProblemType(), CodeSystem.SNOMED_CT.getOid()));

        if (problemObservation.getProblemName() != null) {
            if (entriesReferredToSectionText.contains(ProblemObservation.class)) {
                problemObservationCcd.setText(CcdUtils.createReferenceEntryText(ProblemObservation.class.getSimpleName() + problemObservation.getId()));
            } else {
                problemObservationCcd.setText(CcdUtils.createEntryText(problemObservation.getProblemName()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        problemObservationCcd.setStatusCode(statusCode);

        problemObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(problemObservation.getProblemDateTimeLow(), problemObservation.getProblemDateTimeHigh()));

        // Problem Observation value
        CD ccdCode = CcdUtils.createCDWithDefaultDisplayName(problemObservation.getProblemCode(),
                problemObservation.getProblemName(), CodeSystem.SNOMED_CT.getOid());

        if (problemObservation.getProblemCode() == null)
            ccdCode.setNullFlavor(NullFlavor.OTH);

        // Problem Observation value translations
        if (!CollectionUtils.isEmpty(problemObservation.getTranslations())) {
            for (CcdCode translationCode : problemObservation.getTranslations()) {
                CD translation = CcdUtils.createCD(translationCode);
                ccdCode.getTranslations().add(translation);
            }
        }
        problemObservationCcd.getValues().add(ccdCode);

        // Age Observation
        if (problemObservation.getAgeObservationValue() != null) {
            AgeObservation ageObservation = buildAgeObservation(
                    problemObservation.getAgeObservationUnit(), problemObservation.getAgeObservationValue());
            problemObservationCcd.addObservation(ageObservation);
            ((EntryRelationship) ageObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            ((EntryRelationship) ageObservation.eContainer()).setInversionInd(Boolean.TRUE);
        }

        // Problem Status
        if (problemObservation.getProblemStatusCode() != null || !StringUtils.isEmpty(problemObservation.getProblemStatusText())) {
            ProblemStatus problemStatusObservation = ConsolFactory.eINSTANCE.createProblemStatus();
            problemObservationCcd.addObservation(problemStatusObservation);
            ((EntryRelationship) problemStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            problemStatusObservation.setClassCode(ActClassObservation.OBS);
            problemStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

            II problemStatusObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
            problemStatusObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.6");
            problemStatusObservation.getTemplateIds().add(problemStatusObservationTemplateId);

            CD code = CcdUtils.createCD(SectionTypeCode.STATUS_OBSERVATION);
            problemStatusObservation.setCode(code);

            if (problemObservation.getProblemStatusText() != null) {
                String refId = ProblemObservation.class.getSimpleName() + "Status" + problemObservation.getId();
                ED originalText = DatatypesFactory.eINSTANCE.createED();
                TEL ref = DatatypesFactory.eINSTANCE.createTEL();
                ref.setValue("#" + refId);
                originalText.setReference(ref);
                problemStatusObservation.setText(originalText);
            }

            CS problemStatusObservationStatusCode = DatatypesFactory.eINSTANCE.createCS("completed");
            problemStatusObservation.setStatusCode(problemStatusObservationStatusCode);

            CD problemStatusObservationValue = CcdUtils.createCDWithDefaultDisplayName(problemObservation.getProblemStatusCode(),
                    problemObservation.getProblemStatusText(), CodeSystem.SNOMED_CT.getOid());
            problemStatusObservation.getValues().add(problemStatusObservationValue);
        }

        // Health Status Observation
        if (problemObservation.getHealthStatusCode() != null || !StringUtils.isEmpty(problemObservation.getHealthStatusObservationText())) {
            HealthStatusObservation healthStatusObservation = ConsolFactory.eINSTANCE.createHealthStatusObservation();
            problemObservationCcd.addObservation(healthStatusObservation);
            ((EntryRelationship) healthStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            healthStatusObservation.setClassCode(ActClassObservation.OBS);
            healthStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

            II templateId = DatatypesFactory.eINSTANCE.createII();
            templateId.setRoot("2.16.840.1.113883.10.20.22.4.5");
            healthStatusObservation.getTemplateIds().add(templateId);

            CD code = CcdUtils.createCD(SectionTypeCode.HEALTH_STATUS_OBSERVATION);
            healthStatusObservation.setCode(code);

            if (problemObservation.getHealthStatusObservationText() != null) {
                ED text = DatatypesFactory.eINSTANCE.createED();
                text.addText(problemObservation.getHealthStatusObservationText());
                healthStatusObservation.setText(text);
            }

            CS statusCode1 = DatatypesFactory.eINSTANCE.createCS("completed");
            healthStatusObservation.setStatusCode(statusCode1);

            CD value = CcdUtils.createCDWithDefaultDisplayName(problemObservation.getHealthStatusCode(),
                    problemObservation.getHealthStatusObservationText(), CodeSystem.SNOMED_CT.getOid());
            healthStatusObservation.getValues().add(value);
        }

        return problemObservationCcd;
    }

    // TODO refactoring: DRY, move to shared utils
    public static AgeObservation buildAgeObservation(String unit, Integer value) {
        final AgeObservation ageObservation = ConsolFactory.eINSTANCE.createAgeObservation();

        ageObservation.setClassCode(ActClassObservation.OBS);
        ageObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        ageObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.31"));

        CD ageObservationCode = CcdUtils.createCD(SectionTypeCode.AGE_OBSERVATION);
        ageObservation.setCode(ageObservationCode);

        CS ageObservationStatusCode = DatatypesFactory.eINSTANCE.createCS();
        ageObservationStatusCode.setCode("completed");
        ageObservation.setStatusCode(ageObservationStatusCode);

        PQ ageValue = DatatypesFactory.eINSTANCE.createPQ();
        if (unit != null) {
            ageValue.setUnit(unit);
        } else {
            ageValue.setUnit("a");
        }
        ageValue.setValue(new BigDecimal(value));
        ageObservation.getValues().add(ageValue);

        return ageObservation;
    }

}
