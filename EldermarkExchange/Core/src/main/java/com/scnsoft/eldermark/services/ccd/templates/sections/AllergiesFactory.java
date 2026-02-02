package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation;
import org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.CodeSystem.HL7_ACT_CODE;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.MFST;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.SUBJ;

/**
 * <h1>Alerts (Allergies, Adverse reactions)</h1>
 * “This section is used to list and describe any allergies, adverse reactions, and alerts that are pertinent
 * to the patient’s current or past medical history.” [CCD 3.8]
 *
 * @see Allergy
 * @see AllergyObservation
 */
@Component
public class AllergiesFactory extends RequiredTemplateFactory implements ParsableSectionFactory<AlertsSection, Allergy> {
    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final Logger logger = LoggerFactory.getLogger(AllergiesFactory.class);
    private static final String LEGACY_TABLE = "Allergy_NWHIN";

    @Override
    public AlertsSection buildTemplateInstance(Collection<Allergy> allergies) {
        final AlertsSection alertsSection = CCDFactory.eINSTANCE.createAlertsSection();
        alertsSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.2"));

        final CE sectionCode = CcdUtils.createCE("48765-2", "Allergies, adverse reactions, alerts", CodeSystem.LOINC);
        alertsSection.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Allergies and adverse reactions");
        alertsSection.setTitle(title);

        alertsSection.createStrucDocText(buildSectionText(allergies));

        if (!CollectionUtils.isEmpty(allergies)) {
            for (Allergy allergy : allergies) {
                ProblemAct problemAct = buildAllergyProblemAct(allergy);
                alertsSection.addAct(problemAct);
            }
        } else {
            final ProblemAct problemAct = buildNullAllergyProblemAct();
            alertsSection.addAct(problemAct);
        }

        return alertsSection;
    }

    private ProblemAct buildAllergyProblemAct(Allergy allergy) {
        if (allergy == null) {
            return buildNullAllergyProblemAct();
        }

        ProblemAct problemAct = CCDFactory.eINSTANCE.createProblemAct();
        problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
        problemAct.setMoodCode(x_DocumentActMood.EVN);

        II problemActTemplateId = DatatypesFactory.eINSTANCE.createII();
        problemActTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.30");
        problemAct.getTemplateIds().add(problemActTemplateId);

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

        if (allergy.getAllergyObservations() != null && !allergy.getAllergyObservations().isEmpty()) {
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

    private ProblemAct buildNullAllergyProblemAct() {
        ProblemAct problemAct = CCDFactory.eINSTANCE.createProblemAct();
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

        AlertObservation alertObservation = CCDFactory.eINSTANCE.createAlertObservation();
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
            AlertStatusObservation alertStatusObservation = CCDFactory.eINSTANCE.createAlertStatusObservation();
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

        Collection<com.scnsoft.eldermark.entity.ReactionObservation> reactionObservations = allergyObservation.getReactionObservations();
        if (!CollectionUtils.isEmpty(reactionObservations)) {
            for (com.scnsoft.eldermark.entity.ReactionObservation reactionObservation : reactionObservations) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(MFST);
                entryRelationship1.setInversionInd(true);

                Set<Class> entriesReferredToSectionText = new HashSet<>();
                entriesReferredToSectionText.add(com.scnsoft.eldermark.entity.ReactionObservation.class);
                entriesReferredToSectionText.add(com.scnsoft.eldermark.entity.SeverityObservation.class);

                entryRelationship1.setObservation(SectionEntryFactory.buildReactionObservation(reactionObservation, entriesReferredToSectionText));

                alertObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        com.scnsoft.eldermark.entity.SeverityObservation severityObservation = allergyObservation.getSeverityObservation();
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
        AlertObservation alertObservation = CCDFactory.eINSTANCE.createAlertObservation();

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
                        if (allergyObservation.getObservationStatusCode().getDisplayName() != null)
                            sectionText.append(StringEscapeUtils.escapeHtml4(allergyObservation.getObservationStatusCode().getDisplayName()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("<td>");
                    Collection<com.scnsoft.eldermark.entity.ReactionObservation> reactionObservations = allergyObservation.getReactionObservations();
                    if (!CollectionUtils.isEmpty(reactionObservations)) {
                        String reactionPrefix = "";
                        for (com.scnsoft.eldermark.entity.ReactionObservation reactionObservation : reactionObservations) {
                            if (reactionObservation.getReactionText() != null) {
                                sectionText.append(reactionPrefix);
                                CcdUtils.addReferenceToSectionText(ReactionObservation.class.getSimpleName() + reactionObservation.getId(), reactionObservation.getReactionText(), sectionText);
                                reactionPrefix = "; ";
                                List<com.scnsoft.eldermark.entity.SeverityObservation> severityObservations = reactionObservation.getSeverityObservations();
                                sectionText.append("(");
                                if (severityObservations != null && !severityObservations.isEmpty()) {
                                    String severityPrefix = "";
                                    for (com.scnsoft.eldermark.entity.SeverityObservation severityObservation : severityObservations) {
                                        if (severityObservation.getSeverityText() != null) {
                                            sectionText.append(severityPrefix);
                                            CcdUtils.addReferenceToSectionText(SeverityObservation.class.getSimpleName() + severityObservation.getId(), severityObservation.getSeverityText(), sectionText);
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

    @Override
    public List<Allergy> parseSection(Resident resident, AlertsSection alertsSection) {
        if (!CcdParseUtils.hasContent(alertsSection) || CollectionUtils.isEmpty(alertsSection.getProblemActs())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Allergy> allergies = new ArrayList<>();
        for (ProblemAct ccdProblemAct : alertsSection.getProblemActs()) {
            final Allergy allergy = new Allergy();
            allergy.setResident(resident);
            allergy.setDatabase(resident.getDatabase());
            allergy.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProblemAct.getIds()));

            CD statusCode = ccdProblemAct.getStatusCode();
            allergy.setStatusCode(statusCode != null ? statusCode.getCode() : null);

            Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(ccdProblemAct.getEffectiveTime());
            if (effectiveTime != null) {
                allergy.setTimeHigh(effectiveTime.getFirst());
                allergy.setTimeLow(effectiveTime.getSecond());
            }
            if (!CollectionUtils.isEmpty(ccdProblemAct.getEntryRelationships())) {
                Set<AllergyObservation> allergyObservations = new HashSet<>();
                for (EntryRelationship ccdEntryRelationship : ccdProblemAct.getEntryRelationships()) {
                    if (ccdEntryRelationship.getObservation() != null) {
                        Observation ccdObservation = ccdEntryRelationship.getObservation();
                        AllergyObservation allergyObservation = new AllergyObservation();
                        allergyObservation.setAllergy(allergy);
                        allergyObservation.setDatabase(resident.getDatabase());
                        allergyObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));

                        Pair<Date, Date> obsEffectiveTime = CcdTransform.IVLTStoHighLowDate(ccdObservation.getEffectiveTime());
                        if (obsEffectiveTime != null) {
                            allergyObservation.setTimeHigh(obsEffectiveTime.getFirst());
                            allergyObservation.setTimeLow(obsEffectiveTime.getSecond());
                        }

                        boolean isIHEPCC = false;
                        for (II ii : ccdObservation.getTemplateIds()) {
                            if ("1.3.6.1.4.1.19376.1.5.3.1".equals(ii.getRoot()) && "allergy".equals(ii.getExtension())) {
                                isIHEPCC = true;
                                break;
                            }
                        }

                        if (isIHEPCC) {
                            // According to IHE PCC the value of “Observation / code” in an alert observation represents Alert Type
                            CD ccdObservationCode = ccdObservation.getCode();
                            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationCode));
                            allergyObservation.setAdverseEventTypeText(
                                    CcdTransform.EDtoString(ccdObservationCode.getOriginalText(), allergyObservation.getAdverseEventTypeCode()));
                        } else if (!CollectionUtils.isEmpty(ccdObservation.getValues()) && ccdObservation.getValues().get(0) instanceof CD) {
                            // According to HL7 the value of “Observation / value” in an alert observation represents Alert Type
                            CD ccdObservationValue = (CD) ccdObservation.getValues().get(0);
                            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationValue));
                            allergyObservation.setAdverseEventTypeText(
                                    CcdTransform.EDtoString(ccdObservationValue.getOriginalText(), allergyObservation.getAdverseEventTypeCode()));
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants()) && ccdObservation.getParticipants().get(0).getParticipantRole() != null
                                && ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity() != null) {
                            PlayingEntity ccdPlayingEntity = ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity();
                            if (!CollectionUtils.isEmpty(ccdPlayingEntity.getNames())) {
                                PN name = ccdPlayingEntity.getNames().get(0);
                                allergyObservation.setProductText(name.getText());
                            }
                            allergyObservation.setProductCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getEntryRelationships())) {
                            Set<com.scnsoft.eldermark.entity.ReactionObservation> reactionObservations = new HashSet<>();
                            com.scnsoft.eldermark.entity.SeverityObservation severityObservation = null;
                            for (EntryRelationship entryRelationship : ccdObservation.getEntryRelationships()) {
                                switch (entryRelationship.getTypeCode()) {
                                    case MFST:
                                        com.scnsoft.eldermark.entity.ReactionObservation reactionObservation =
                                                sectionEntryParseFactory.parseReactionObservation(entryRelationship.getObservation(), resident,
                                                        LEGACY_TABLE);
                                        if (reactionObservation != null) {
                                            reactionObservations.add(reactionObservation);
                                        }
                                        break;
                                    case SUBJ:
                                        if (severityObservation != null) {
                                            continue;
                                        }
                                        severityObservation = sectionEntryParseFactory.parseSeverityObservation(
                                                entryRelationship.getObservation(), resident, LEGACY_TABLE);
                                        break;
                                    case REFR:
                                        try {
                                            CD observationValue = ObservationFactory.getValue(entryRelationship.getObservation(), CD.class);
                                            allergyObservation.setObservationStatusCode(ccdCodeFactory.convert(observationValue));
                                        } catch (ClassCastException exc) {
                                            exc.printStackTrace();
                                        }
                                        break;
                                }
                            }
                            allergyObservation.setReactionObservations(reactionObservations);
                            allergyObservation.setSeverityObservation(severityObservation);
                        }
                        allergyObservations.add(allergyObservation);
                    }
                }
                allergy.setAllergyObservations(allergyObservations);
            }
            allergies.add(allergy);
        }

        return allergies;
    }

}