package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.codes.ProblemActStatusCode;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_ActClassDocumentEntryAct;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProblemConcernAct;
import org.openhealthtools.mdht.uml.cda.consol.ProblemSection;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <h1>Problems</h1> “This section lists and describes all relevant clinical
 * problems at the time the summary is generated. At a minimum, all pertinent
 * current and historical problems should be listed.” [CCD 3.5]
 *
 * @see Problem
 * @see ProblemObservation
 */
@Component("consol.ProblemsFactory")
public class ProblemsFactory extends RequiredTemplateFactory implements SectionFactory<ProblemSection, Problem> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.5.1";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

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

    private static String buildSectionText(Collection<Problem> problems) {

        if (CollectionUtils.isEmpty(problems)) {
            return "No known problems.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Problem Name</th>" +
                "<th>Dates</th>" +
                "<th>Status</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Problem problem : problems) {
            if (CollectionUtils.isNotEmpty(problem.getProblemObservations())) {
                for (ProblemObservation problemObservation : problem.getProblemObservations()) {
                    body.append("<tr>");
                    body.append("<td>");
                    //todo should be notEmpty instead of null?
                    if (problemObservation.getProblemName() != null) {
                        CcdUtils.addReferenceToSectionText(
                                ProblemObservation.class.getSimpleName() + problemObservation.getId(),
                                problemObservation.getProblemName(), body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    if (problemObservation.getProblemDateTimeLow() != null
                            || problemObservation.getProblemDateTimeHigh() != null) {
                        CcdUtils.addDateRangeToSectionText(problemObservation.getProblemDateTimeLow(),
                                problemObservation.getProblemDateTimeHigh(), body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    //todo should be notEmpty instead of null?
                    if (problemObservation.getProblemStatusText() != null) {
                        CcdUtils.addReferenceToSectionText(
                                ProblemObservation.class.getSimpleName() + "Status" + problemObservation.getId(),
                                problemObservation.getProblemStatusText(), body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");
                    body.append("</tr>");
                }
            }
        }

        if (body.length() == 0) {
            return "No known problems.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
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
            /*if(problem.getStatusCode()==null) { //todo [ccd] review below
                problemActStatusCode=CcdUtils.createCS("completed");
            }
            else {
                problemActStatusCode = CcdUtils.createCS(problem.getStatusCode());
            }*/
        } else {
            problemActStatusCode = CcdUtils.createCS(actStatusCode);
        }
        problemAct.setStatusCode(problemActStatusCode);

        if (problem.getTimeLow() != null || problem.getTimeHigh() != null) {
            problemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(problem.getTimeLow(), problem.getTimeHigh(), true, false));
        } else {
            problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        }

        List<ProblemObservation> problemObservations = problem.getProblemObservations();
        if (!CollectionUtils.isEmpty(problemObservations)) {
            for (ProblemObservation problemObservation : problemObservations) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);

                Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
                entriesReferredToSectionText.add(ProblemObservation.class);

                entryRelationship
                        .setObservation(consolSectionEntryFactory.buildProblemObservation(problemObservation, entriesReferredToSectionText));
                problemAct.getEntryRelationships().add(entryRelationship);
            }
        } else {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setObservation(consolSectionEntryFactory.buildNullProblemObservation());
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
//        problemStatusCode.setCode("completed"); //todo review
        problemAct.setStatusCode(problemStatusCode);

        problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        entryRelationship.setObservation(consolSectionEntryFactory.buildNullProblemObservation());
        problemAct.getEntryRelationships().add(entryRelationship);

        return problemAct;
    }
}
