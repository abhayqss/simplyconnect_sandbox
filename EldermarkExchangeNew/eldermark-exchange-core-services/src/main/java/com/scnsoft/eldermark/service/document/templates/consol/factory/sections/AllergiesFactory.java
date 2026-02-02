package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.openhealthtools.mdht.uml.cda.consol.AllergyStatusObservation;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.scnsoft.eldermark.entity.document.ccd.CodeSystem.HL7_ACT_CODE;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.MFST;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.SUBJ;

/**
 * <h1>Allergies, Adverse reactions</h1> “This section is used to list and
 * describe any allergies, adverse reactions, and alerts that are pertinent to
 * the patient’s current or past medical history.” [CCD 3.8]
 *
 * @see Allergy
 */
@Component("consol.AllergiesFactory")
public class AllergiesFactory extends RequiredTemplateFactory implements SectionFactory<AllergiesSection, Allergy> {

    // private static final String LEGACY_TABLE = "Allergy_NWHIN";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.6.1";

    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

    private static String buildSectionText(Collection<Allergy> allergies) {

        if (CollectionUtils.isEmpty(allergies)) {
            return "No known allergies.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Substance</th>" +
                "<th>Status</th>" +
                "<th>Reaction (Severity)</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Allergy allergy : allergies) {
            if (CollectionUtils.isNotEmpty(allergy.getAllergyObservations())) {
                for (AllergyObservation allergyObservation : allergy.getAllergyObservations()) {
                    body.append("<tr>");
                    body.append("<td>");
                    if (allergyObservation.getProductText() != null) {
                        body.append(StringEscapeUtils.escapeHtml(allergyObservation.getProductText()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    if (allergyObservation.getObservationStatusCode() != null) {
                        if (allergyObservation.getObservationStatusCode().getDisplayName() != null)
                            body.append(StringEscapeUtils
                                    .escapeHtml(allergyObservation.getObservationStatusCode().getDisplayName()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    Collection<ReactionObservation> reactionObservations = allergyObservation
                            .getReactionObservations();
                    if (!CollectionUtils.isEmpty(reactionObservations)) {
                        String reactionPrefix = "";
                        for (ReactionObservation reactionObservation : reactionObservations) {
                            if (reactionObservation.getReactionText() != null) {
                                body.append(reactionPrefix);
                                CcdUtils.addReferenceToSectionText(
                                        ReactionObservation.class.getSimpleName() + reactionObservation.getId(),
                                        reactionObservation.getReactionText(), body);
                                reactionPrefix = "; ";
                                List<SeverityObservation> severityObservations = reactionObservation
                                        .getSeverityObservations();
                                body.append("(");
                                if (CollectionUtils.isNotEmpty(severityObservations)) {
                                    String severityPrefix = "";
                                    for (SeverityObservation severityObservation : severityObservations) {
                                        if (severityObservation.getSeverityText() != null) {
                                            body.append(severityPrefix);
                                            CcdUtils.addReferenceToSectionText(
                                                    SeverityObservation.class.getSimpleName() + severityObservation.getId(),
                                                    severityObservation.getSeverityText(), body);
                                            severityPrefix = "; ";
                                        }
                                    }
                                } else {
                                    CcdUtils.addEmptyCellToSectionText(body);
                                }
                                body.append(")");
                            }
                        }
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");
                    body.append("</tr>");
                }
            }
        }

        if (body.length() == 0) {
            return "No known allergies.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);
        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public AllergiesSection buildTemplateInstance(Collection<Allergy> allergies) {
        final AllergiesSection alertsSection = ConsolFactory.eINSTANCE.createAllergiesSection();
        alertsSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("48765-2", "Allergies, adverse reactions, alerts", CodeSystem.LOINC);
        alertsSection.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Allergies and adverse reactions");
        alertsSection.setTitle(title);

        alertsSection.createStrucDocText(buildSectionText(allergies));

        if (CollectionUtils.isNotEmpty(allergies)) {
            for (Allergy allergy : allergies) {
                AllergyProblemAct problemAct = buildAllergyProblemAct(allergy);
                alertsSection.addAct(problemAct);
            }
        } else {
            final AllergyProblemAct problemAct = buildNullAllergyProblemAct();
            alertsSection.addAct(problemAct);
        }

        return alertsSection;
    }

    private AllergyProblemAct buildAllergyProblemAct(Allergy allergy) {
        if (allergy == null) {
            return buildNullAllergyProblemAct();
        }

        final AllergyProblemAct problemAct = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
        problemAct.setMoodCode(x_DocumentActMood.EVN);

        problemAct.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.30"));

        if (allergy.getId() != null) {
            problemAct.getIds().add(CcdUtils.getId(allergy.getId()));
        } else {
            problemAct.getIds().add(CcdUtils.getNullId());
        }

        CE problemActCode = CcdUtils.createCE("48765-2", "Allergies, adverse reactions, alerts", CodeSystem.LOINC);
        problemAct.setCode(problemActCode);

        CS problemStatusCode = DatatypesFactory.eINSTANCE.createCS();
        if (allergy.getStatusCode() != null) {
            problemStatusCode.setCode(allergy.getStatusCode());
        } else {
            problemStatusCode.setNullFlavor(NullFlavor.UNK);
            problemStatusCode.setCode("completed");
        }
        problemAct.setStatusCode(problemStatusCode);

        if (allergy.getTimeLow() != null || allergy.getTimeHigh() != null) {
            problemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy.getTimeLow(), allergy.getTimeHigh(),
                    "active".equalsIgnoreCase(allergy.getStatusCode()), //CONF:7504
                    "completed".equalsIgnoreCase(allergy.getStatusCode()) //CONF:10085
            ));
        } else {
            problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        }

        if (!CollectionUtils.isEmpty(allergy.getAllergyObservations())) {
            for (AllergyObservation allergyObservation : allergy.getAllergyObservations()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(SUBJ);
                entryRelationship.setObservation(consolSectionEntryFactory.buildAllergyObservation(allergyObservation));
                problemAct.getEntryRelationships().add(entryRelationship);
            }
        } else {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(SUBJ);
            entryRelationship.setObservation(consolSectionEntryFactory.buildNullAllergyObservation());
            problemAct.getEntryRelationships().add(entryRelationship);
        }

        return problemAct;
    }

    private AllergyProblemAct buildNullAllergyProblemAct() {
        AllergyProblemAct problemAct = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
        problemAct.setMoodCode(x_DocumentActMood.EVN);

        II problemActTemplateId = DatatypesFactory.eINSTANCE.createII();
        problemActTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.30");
        problemAct.getTemplateIds().add(problemActTemplateId);

        problemAct.getIds().add(CcdUtils.getNullId());

        CE problemActCode = DatatypesFactory.eINSTANCE.createCE();
        problemActCode.setCode("48765-2");
        problemActCode.setCodeSystem("2.16.840.1.113883.6.1");
        problemActCode.setDisplayName("Allergies, adverse reactions, alerts");
        problemActCode.setCodeSystemName("LOINC");
        problemAct.setCode(problemActCode);

        CS problemStatusCode = DatatypesFactory.eINSTANCE.createCS();
        //todo [ccd] should be one of active, suspended, aborted, completed
        problemStatusCode.setNullFlavor(NullFlavor.NI);
        problemAct.setStatusCode(problemStatusCode);

        problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(SUBJ);
        entryRelationship.setObservation(consolSectionEntryFactory.buildNullAllergyObservation());
        problemAct.getEntryRelationships().add(entryRelationship);

        return problemAct;
    }
}