package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.ReactionObservation;
import com.scnsoft.eldermark.entity.document.ccd.SeverityObservation;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.parser.entries.SectionEntryParseFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.document.ccd.CodeSystem.HL7_ACT_CODE;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.MFST;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.SUBJ;

/**
 * <h1>Alerts (Allergies, Adverse reactions)</h1> “This section is used to list
 * and describe any allergies, adverse reactions, and alerts that are pertinent
 * to the patient’s current or past medical history.” [CCD 3.8]
 *
 * @see Allergy
 * @see AllergyObservation
 */
@Component
public class AllergiesFactory extends RequiredTemplateFactory
        implements ParsableSectionFactory<AlertsSection, Allergy> {
    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final String LEGACY_TABLE = "Allergy_NWHIN";

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
    public AlertsSection buildTemplateInstance(Collection<Allergy> allergies) {
        final AlertsSection alertsSection = CCDFactory.eINSTANCE.createAlertsSection();
        alertsSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.2"));

        final CE sectionCode = CcdUtils.createCE("48765-2", "Allergies, adverse reactions, alerts", CodeSystem.LOINC);
        alertsSection.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Allergies and adverse reactions");
        alertsSection.setTitle(title);

        alertsSection.createStrucDocText(buildSectionText(allergies));

        if (CollectionUtils.isNotEmpty(allergies)) {
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
        entryRelationship.setObservation(ccdSectionEntryFactory.buildNullAllergyObservation());
        problemAct.getEntryRelationships().add(entryRelationship);

        return problemAct;
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
            problemStatusCode.setNullFlavor(NullFlavor.UNK);
            problemStatusCode.setCode("completed");
        }
        problemAct.setStatusCode(problemStatusCode);

        if (allergy.getTimeLow() != null || allergy.getTimeHigh() != null) {
            //below CONF restrictions are actually for C-CDA CCD R1.1
            problemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy.getTimeLow(), allergy.getTimeHigh(),
                    "active".equalsIgnoreCase(allergy.getStatusCode()), //CONF:7504
                    "completed".equalsIgnoreCase(allergy.getStatusCode()) //CONF:10085
            ));
        } else {
            problemAct.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        }

        if (CollectionUtils.isNotEmpty(allergy.getAllergyObservations())) {
            for (AllergyObservation allergyObservation : allergy.getAllergyObservations()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(SUBJ);
                entryRelationship.setObservation(ccdSectionEntryFactory.buildAllergyObservation(allergyObservation));
                problemAct.getEntryRelationships().add(entryRelationship);
            }
        } else {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(SUBJ);
            entryRelationship.setObservation(ccdSectionEntryFactory.buildNullAllergyObservation());
            problemAct.getEntryRelationships().add(entryRelationship);
        }

        return problemAct;
    }

    @Override
    public List<Allergy> parseSection(Client client, AlertsSection alertsSection) {
        if (!CcdParseUtils.hasContent(alertsSection) || CollectionUtils.isEmpty(alertsSection.getProblemActs())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Allergy> allergies = new ArrayList<>();
        for (ProblemAct ccdProblemAct : alertsSection.getProblemActs()) {
            final Allergy allergy = new Allergy();
            allergy.setClient(client);
            allergy.setOrganization(client.getOrganization());
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
                        allergyObservation.setOrganization(client.getOrganization());
                        allergyObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));

                        Pair<Date, Date> obsEffectiveTime = CcdTransform
                                .IVLTStoHighLowDate(ccdObservation.getEffectiveTime());
                        if (obsEffectiveTime != null) {
                            allergyObservation.setTimeHigh(obsEffectiveTime.getFirst());
                            allergyObservation.setTimeLow(obsEffectiveTime.getSecond());
                        }

                        boolean isIHEPCC = false;
                        for (II ii : ccdObservation.getTemplateIds()) {
                            if ("1.3.6.1.4.1.19376.1.5.3.1".equals(ii.getRoot())
                                    && "allergy".equals(ii.getExtension())) {
                                isIHEPCC = true;
                                break;
                            }
                        }

                        if (isIHEPCC) {
                            // According to IHE PCC the value of “Observation / code” in an alert
                            // observation represents Alert Type
                            CD ccdObservationCode = ccdObservation.getCode();
                            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationCode));
                            allergyObservation.setAdverseEventTypeText(
                                    CcdTransform.EDtoString(ccdObservationCode.getOriginalText(),
                                            allergyObservation.getAdverseEventTypeCode()));
                        } else if (!CollectionUtils.isEmpty(ccdObservation.getValues())
                                && ccdObservation.getValues().get(0) instanceof CD) {
                            // According to HL7 the value of “Observation / value” in an alert observation
                            // represents Alert Type
                            CD ccdObservationValue = (CD) ccdObservation.getValues().get(0);
                            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationValue));
                            allergyObservation.setAdverseEventTypeText(
                                    CcdTransform.EDtoString(ccdObservationValue.getOriginalText(),
                                            allergyObservation.getAdverseEventTypeCode()));
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants())
                                && ccdObservation.getParticipants().get(0).getParticipantRole() != null
                                && ccdObservation.getParticipants().get(0).getParticipantRole()
                                .getPlayingEntity() != null) {
                            PlayingEntity ccdPlayingEntity = ccdObservation.getParticipants().get(0)
                                    .getParticipantRole().getPlayingEntity();
                            if (!CollectionUtils.isEmpty(ccdPlayingEntity.getNames())) {
                                PN name = ccdPlayingEntity.getNames().get(0);
                                allergyObservation.setProductText(name.getText());
                            }
                            allergyObservation.setProductCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getEntryRelationships())) {
                            Set<ReactionObservation> reactionObservations = new HashSet<>();
                            SeverityObservation severityObservation = null;
                            for (EntryRelationship entryRelationship : ccdObservation.getEntryRelationships()) {
                                switch (entryRelationship.getTypeCode()) {
                                    case MFST:
                                        ReactionObservation reactionObservation = sectionEntryParseFactory
                                                .parseReactionObservation(entryRelationship.getObservation(), client,
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
                                                entryRelationship.getObservation(), client, LEGACY_TABLE);
                                        break;
                                    case REFR:
                                        try {
                                            CD observationValue = ObservationFactory
                                                    .getValue(entryRelationship.getObservation(), CD.class);
                                            allergyObservation
                                                    .setObservationStatusCode(ccdCodeFactory.convert(observationValue));
                                        } catch (ClassCastException exc) {

                                        }
                                        break;
                                    default:
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