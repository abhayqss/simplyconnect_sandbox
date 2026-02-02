package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.openhealthtools.mdht.uml.cda.consol.AllergyStatusObservation;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.scnsoft.eldermark.entity.CodeSystem.HL7_ACT_CODE;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.MFST;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.SUBJ;

/**
 * <h1>Allergies, Adverse reactions</h1>
 * “This section is used to list and describe any allergies, adverse reactions, and alerts that are pertinent
 * to the patient’s current or past medical history.” [CCD 3.8]
 *
 * @see Allergy
 */
@Component("consol.AllergiesFactory")
public class AllergiesFactory extends RequiredTemplateFactory implements SectionFactory<AllergiesSection, Allergy> {

    private static final Logger logger = LoggerFactory.getLogger(AllergiesFactory.class);
    private static final String LEGACY_TABLE = "Allergy_NWHIN";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.6.1";

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

        if (!CollectionUtils.isEmpty(allergies)) {
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
            problemStatusCode.setNullFlavor(NullFlavor.NI);
        }
        problemAct.setStatusCode(problemStatusCode);

        if (allergy.getTimeLow() != null || allergy.getTimeHigh() != null) {
            problemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy.getTimeLow(), allergy.getTimeHigh()));
        } else {
            problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        }

        if (!CollectionUtils.isEmpty(allergy.getAllergyObservations())) {
            for (AllergyObservation allergyObservation : allergy.getAllergyObservations()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(SUBJ);
                entryRelationship.setObservation(buildAllergyObservation(allergyObservation));
                problemAct.getEntryRelationships().add(entryRelationship);
            }
        } else {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(SUBJ);
            entryRelationship.setObservation(buildNullAllergyObservation());
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
        problemStatusCode.setNullFlavor(NullFlavor.NI);
        problemAct.setStatusCode(problemStatusCode);

        problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(SUBJ);
        entryRelationship.setObservation(buildNullAllergyObservation());
        problemAct.getEntryRelationships().add(entryRelationship);

        return problemAct;
    }

    private Observation buildAllergyObservation(AllergyObservation allergyObservation) {
        if (allergyObservation == null) {
            return buildNullAllergyObservation();
        }

        org.openhealthtools.mdht.uml.cda.consol.AllergyObservation alertObservation = ConsolFactory.eINSTANCE.createAllergyObservation();
        alertObservation.setClassCode(ActClassObservation.OBS);
        alertObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
        alertObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.7"));
        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        alertObservation.setStatusCode(statusCode);
        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("ASSERTION");
        code.setCodeSystem(HL7_ACT_CODE.getOid());
        alertObservation.setCode(code);

        alertObservation.getIds().add(CcdUtils.getId(allergyObservation.getId()));
        CcdUtils.addConsanaId(alertObservation.getIds(), allergyObservation.getConsanaId());

        Date timeLow = allergyObservation.getTimeLow();
        Date timeHigh = allergyObservation.getTimeHigh();
        if (timeLow == null && timeHigh == null) {
            alertObservation.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        } else {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            alertObservation.setEffectiveTime(effectiveTime);
            if (timeLow != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
                effectiveTime.setLow(low);
            } else {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setNullFlavor(NullFlavor.UNK);
                effectiveTime.setLow(low);
            }
            if (timeHigh != null) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                effectiveTime.setHigh(high);
            }
        }

        CE allergyTypeValue = CcdUtils.createCEWithDefaultDisplayName(allergyObservation.getAdverseEventTypeCode(),
                allergyObservation.getAdverseEventTypeText(),
                CodeSystem.SNOMED_CT.getOid());

        if (allergyObservation.getAdverseEventTypeText() != null &&
                !StringUtils.equals(allergyObservation.getAdverseEventTypeText(), allergyTypeValue.getDisplayName())) {
            ED originalText = DatatypesFactory.eINSTANCE.createED();
            originalText.addText(allergyObservation.getAdverseEventTypeText());
            allergyTypeValue.setOriginalText(originalText);
        }
        alertObservation.getValues().add(allergyTypeValue);

        if (allergyObservation.getProductText() != null || allergyObservation.getProductCode() != null) {
            Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
            participant2.setTypeCode(ParticipationType.CSM);
            ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
            participantRole.setClassCode(RoleClassRoot.MANU);
            PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
            playingEntity.setClassCode(EntityClassRoot.MMAT);

            if (allergyObservation.getProductText() != null) {
                PN productName = DatatypesFactory.eINSTANCE.createPN();
                productName.addText(allergyObservation.getProductText());
                playingEntity.getNames().add(productName);
            }

            playingEntity.setCode(CcdUtils.createCEWithDefaultDisplayName(allergyObservation.getProductCode(),
                    allergyObservation.getProductText()));

            participantRole.setPlayingEntity(playingEntity);
            participant2.setParticipantRole(participantRole);
            alertObservation.getParticipants().add(participant2);
        }

        if (allergyObservation.getObservationStatusCode() != null) {
            AllergyStatusObservation alertStatusObservation = ConsolFactory.eINSTANCE.createAllergyStatusObservation();
            alertObservation.addObservation(alertStatusObservation);
            ((EntryRelationship) alertStatusObservation.eContainer()).setTypeCode(SUBJ);
            ((EntryRelationship) alertStatusObservation.eContainer()).setInversionInd(Boolean.TRUE);
            alertStatusObservation.setClassCode(ActClassObservation.OBS);
            alertStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
            II alertStatusObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
            alertStatusObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.28");
            alertStatusObservation.getTemplateIds().add(alertStatusObservationTemplateId);
            CS alertStatusObservationStatusCode = DatatypesFactory.eINSTANCE.createCS("completed");
            alertStatusObservation.setStatusCode(alertStatusObservationStatusCode);

            CD alertStatusObservationCode = CcdUtils.createCD(SectionTypeCode.STATUS_OBSERVATION);
            alertStatusObservation.setCode(alertStatusObservationCode);

            alertStatusObservation.getValues().add(CcdUtils.createCE(allergyObservation.getObservationStatusCode(), CodeSystem.SNOMED_CT.getOid()));
        }

        Collection<ReactionObservation> reactionObservations = allergyObservation.getReactionObservations();
        if (!CollectionUtils.isEmpty(reactionObservations)) {
            for (ReactionObservation reactionObservation : reactionObservations) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(MFST);
                entryRelationship1.setInversionInd(true);

                Set<Class> entriesReferredToSectionText = new HashSet<>();
                entriesReferredToSectionText.add(ReactionObservation.class);
                entriesReferredToSectionText.add(SeverityObservation.class);

                entryRelationship1.setObservation(SectionEntryFactory.buildReactionObservation(reactionObservation, entriesReferredToSectionText));

                alertObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        SeverityObservation severityObservation = allergyObservation.getSeverityObservation();
        if (severityObservation != null) {
            EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship1.setTypeCode(SUBJ);
            entryRelationship1.setInversionInd(true);

            Set<Class> entriesReferredToSectionText = new HashSet<>();
            entryRelationship1.setObservation(SectionEntryFactory.buildSeverityObservation(severityObservation, entriesReferredToSectionText));

            alertObservation.getEntryRelationships().add(entryRelationship1);
        }

        return alertObservation;
    }

    private Observation buildNullAllergyObservation() {
        org.openhealthtools.mdht.uml.cda.consol.AllergyObservation alertObservation = ConsolFactory.eINSTANCE.createAllergyObservation();

        alertObservation.setClassCode(ActClassObservation.OBS);
        alertObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.7");
        alertObservation.getTemplateIds().add(alertObservationTemplateId);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        alertObservation.setStatusCode(statusCode);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("ASSERTION");
        code.setCodeSystem(HL7_ACT_CODE.getOid());
        alertObservation.setCode(code);

        alertObservation.getIds().add(CcdUtils.getNullId());

        alertObservation.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        CE allergyTypeValue = DatatypesFactory.eINSTANCE.createCE();
        allergyTypeValue.setNullFlavor(NullFlavor.NI);
        alertObservation.getValues().add(allergyTypeValue);

        return alertObservation;
    }

    private static String buildSectionText(Collection<Allergy> allergies) {

        if (CollectionUtils.isEmpty(allergies)) {
            return "No known allergies.";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Substance</th>");
        sectionText.append("<th>Status</th>");
        sectionText.append("<th>Reaction (Severity)</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Allergy allergy : allergies) {
            if (allergy.getAllergyObservations() != null) {
                sectionText.append("<tr>");
                for (AllergyObservation allergyObservation : allergy.getAllergyObservations()) {
                    sectionText.append("<td>");
                    if (allergyObservation.getProductText() != null) {
                        sectionText.append(StringEscapeUtils.escapeHtml4(allergyObservation.getProductText()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("<td>");
                    if (allergyObservation.getObservationStatusCode() != null) {
                        if (allergyObservation.getObservationStatusCode().getDisplayName() != null) {
                            sectionText.append(StringEscapeUtils.escapeHtml4(allergyObservation.getObservationStatusCode().getDisplayName()));
                        }
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("<td>");
                    Collection<ReactionObservation> reactionObservations = allergyObservation.getReactionObservations();
                    if (!CollectionUtils.isEmpty(reactionObservations)) {
                        String reactionPrefix = "";
                        for (ReactionObservation reactionObservation : reactionObservations) {
                            if (reactionObservation.getReactionText() != null) {
                                sectionText.append(reactionPrefix);
                                CcdUtils.addReferenceToSectionText(
                                        org.openhealthtools.mdht.uml.cda.consol.ReactionObservation.class.getSimpleName() + reactionObservation.getId(),
                                        reactionObservation.getReactionText(), sectionText);
                                reactionPrefix = "; ";
                                List<SeverityObservation> severityObservations = reactionObservation.getSeverityObservations();
                                sectionText.append("(");
                                if (severityObservations != null && !severityObservations.isEmpty()) {
                                    String severityPrefix = "";
                                    for (SeverityObservation severityObservation : severityObservations) {
                                        if (severityObservation.getSeverityText() != null) {
                                            sectionText.append(severityPrefix);
                                            CcdUtils.addReferenceToSectionText(
                                                    org.openhealthtools.mdht.uml.cda.consol.SeverityObservation.class.getSimpleName() + severityObservation.getId(),
                                                    severityObservation.getSeverityText(), sectionText);
                                            severityPrefix = "; ";
                                        }
                                    }
                                } else {
                                    CcdUtils.addEmptyCellToSectionText(sectionText);
                                }
                                sectionText.append(")");
                            }
                        }
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

}