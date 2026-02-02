package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.document.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.document.ccd.AdvanceDirective;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectivesSection;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * <h1>Advance Directives</h1> “This section contains data defining the
 * patient’s advance directives and any reference to supporting documentation...
 * This section contains data such as the existence of living wills, healthcare
 * proxies, and CPR and resuscitation status.” [CCD 3.2]
 *
 * @see AdvanceDirective
 * @see AdvanceDirectiveDocument
 * @see Organization
 * @see Name
 * @see Participant
 * @see Person
 * @see Client
 */
@Component("consol.AdvanceDirectiveFactory")
public class AdvanceDirectiveFactory extends OptionalTemplateFactory
        implements SectionFactory<AdvanceDirectivesSection, AdvanceDirective> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.21";

    @Value("${section.advanceDirectives.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public AdvanceDirectivesSection buildTemplateInstance(Collection<AdvanceDirective> advanceDirectives) {
        final AdvanceDirectivesSection advanceDirectiveSection = ConsolFactory.eINSTANCE
                .createAdvanceDirectivesSection();
        advanceDirectiveSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        CE sectionCode = CcdUtils.createCE("42348-3", "Advance Directives", CodeSystem.LOINC);
        advanceDirectiveSection.setCode(sectionCode);

        ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Advance Directives");
        advanceDirectiveSection.setTitle(title);

        advanceDirectiveSection.createStrucDocText(buildSectionText(advanceDirectives));

        if (CollectionUtils.isEmpty(advanceDirectives)) {
            return advanceDirectiveSection;
        }

        for (AdvanceDirective advanceDirective : advanceDirectives) {
            org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectiveObservation advanceDirectiveObservation = ConsolFactory.eINSTANCE
                    .createAdvanceDirectiveObservation();
            advanceDirectiveObservation.setClassCode(ActClassObservation.OBS);
            advanceDirectiveObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
            advanceDirectiveSection.addObservation(advanceDirectiveObservation);

            II adoTemplateId = DatatypesFactory.eINSTANCE.createII();
            adoTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.48");
            advanceDirectiveObservation.getTemplateIds().add(adoTemplateId);

            advanceDirectiveObservation.getIds().add(CcdUtils.getId(advanceDirective.getId()));

            advanceDirectiveObservation.setCode(CcdUtils.createCD(advanceDirective.getType(), CodeSystem.SNOMED_CT.getOid()));

            CS statusCode = DatatypesFactory.eINSTANCE.createCS();
            statusCode.setCode("completed");
            advanceDirectiveObservation.setStatusCode(statusCode);

            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
            if (advanceDirective.getTimeLow() != null) {
                low.setValue(CcdUtils.formatSimpleDate(advanceDirective.getTimeLow()));
            } else {
                low.setNullFlavor(NullFlavor.UNK);
            }
            effectiveTime.setLow(low);
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            if (advanceDirective.getTimeHigh() != null) {
                high.setValue(CcdUtils.formatSimpleDate(advanceDirective.getTimeHigh()));
            } else {
                high.setNullFlavor(NullFlavor.UNK);
            }
            effectiveTime.setHigh(high);
            advanceDirectiveObservation.setEffectiveTime(effectiveTime);

            if (advanceDirective.getVerifiers() != null) {
                for (Participant verifier : advanceDirective.getVerifiers()) {
                    Participant2 participant = CDAFactory.eINSTANCE.createParticipant2();
                    participant.setTypeCode(ParticipationType.VRF);

                    II participantTemplateId = DatatypesFactory.eINSTANCE.createII();
                    participantTemplateId.setRoot("2.16.840.1.113883.10.20.1.58");
                    participant.getTemplateIds().add(participantTemplateId);

                    if (verifier.getTimeLow() != null) {
                        participant.setTime(CcdUtils.convertEffectiveTime(verifier.getTimeLow()));
                    } else {
                        participant.setTime(CcdUtils.getNullEffectiveTime());
                    }

                    ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                    participant.setParticipantRole(participantRole);
//                  participantRole.setClassCode(RoleClassRoot.MANU);

                    participantRole.getIds().add(CcdUtils.getId(verifier.getId()));

                    PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
                    participantRole.setPlayingEntity(playingEntity);

                    if (verifier.getPerson() != null && verifier.getPerson().getNames() != null) {
                        for (Name verName : verifier.getPerson().getNames()) {
                            CcdUtils.addConvertedName(playingEntity.getNames(), verName);
                        }
                    } else {
                        playingEntity.getNames().add(CcdUtils.getNullName());
                    }

                    advanceDirectiveObservation.getParticipants().add(participant);
                }
            }

            Participant custodian = advanceDirective.getCustodian();

            if (custodian != null) {

                Participant2 custodianCcd = CDAFactory.eINSTANCE.createParticipant2();
                custodianCcd.setTypeCode(ParticipationType.CST);
                ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                custodianCcd.setParticipantRole(participantRole);
                participantRole.setClassCode(RoleClassRoot.AGNT);

                Person person = custodian.getPerson();

                if (person != null) {
                    // get custodian addresses, telecoms, and name from person
                    CcdUtils.addConvertedAddresses(person.getAddresses(), participantRole.getAddrs(), false);
                    CcdUtils.addConvertedTelecoms(person.getTelecoms(), participantRole.getTelecoms(), false);

                    PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
                    participantRole.setPlayingEntity(playingEntity);

                    CcdUtils.addConvertedNames(person.getNames(), playingEntity.getNames(), true);

                    advanceDirectiveObservation.getParticipants().add(custodianCcd);
                }
            }

            List<AdvanceDirectiveDocument> referenceDocuments = advanceDirective.getReferenceDocuments();

            if (!CollectionUtils.isEmpty(referenceDocuments)) {

                for (AdvanceDirectiveDocument document : referenceDocuments) {

                    Reference reference = CDAFactory.eINSTANCE.createReference();
                    reference.setTypeCode(x_ActRelationshipExternalReference.REFR);

                    ExternalDocument externalDocument = CDAFactory.eINSTANCE.createExternalDocument();
                    reference.setExternalDocument(externalDocument);

                    externalDocument.getIds().add(CcdUtils.getId(document.getId()));

                    if (document.getMediaType() != null || document.getUrl() != null) {

                        ED externalDocumentText = DatatypesFactory.eINSTANCE.createED();
                        externalDocument.setText(externalDocumentText);

                        if (document.getMediaType() != null) {
                            externalDocumentText.setMediaType(document.getMediaType());
                        }

                        if (document.getUrl() != null) {
                            TEL referenceUrl = DatatypesFactory.eINSTANCE.createTEL(document.getUrl());
                            externalDocumentText.setReference(referenceUrl);
                        }
                        advanceDirectiveObservation.getReferences().add(reference);
                    }

                }
            }

        }
        return advanceDirectiveSection;
    }

    private static String buildSectionText(Collection<AdvanceDirective> advanceDirectives) {

        if (CollectionUtils.isEmpty(advanceDirectives)) {
            return "No known advance directives.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Directive</th>" +
                "<th>Verification</th>" +
                "<th>Supporting Document(s)</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (AdvanceDirective advanceDirective : advanceDirectives) {
            body.append("<tr>");

            CcdUtils.addCellToSectionText(advanceDirective.getType(), body);

            body.append("<td>");
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(advanceDirective.getVerifiers())) {
                for (Participant verifier : advanceDirective.getVerifiers()) {
                    if (verifier.getPerson() != null) {
                        boolean added = false;
                        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(verifier.getPerson().getNames())) {
                            Name name = verifier.getPerson().getNames().get(0);
                            body.append(StringEscapeUtils
                                    .escapeHtml(name.getPrefix() + " " + name.getGiven() + " " + name.getFamily()));
                            added = true;
                        }
                        if (verifier.getTimeLow() != null) {
                            if (added) {
                                body.append(", ");
                            } else {
                                added = true;
                            }
                            body.append(CcdUtils.formatDate(verifier.getTimeLow()));
                        }
                        if (added) {
                            body.append("<br/>");
                        }
                    }
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(advanceDirective.getReferenceDocuments())) {
                for (AdvanceDirectiveDocument document : advanceDirective.getReferenceDocuments()) {
                    if (document.getUrl() != null) {
                        body.append("<linkHtml href=\"").append(StringEscapeUtils.escapeHtml(document.getUrl()))
                                .append("\">Advance directive</linkHtml>");
                    }

                }

            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");
            body.append("</tr>");
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);
        sectionText.append("</table>");

        return sectionText.toString();
    }

}