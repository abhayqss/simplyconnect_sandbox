package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PregnancyObservation;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ActClassObservation;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.SocialHistorySection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * <h1>Social History</h1> “This section contains data defining the patient’s
 * occupational, personal (e.g. lifestyle), social, and environmental history
 * and health risk factors, as well as administrative data such as marital
 * status, race, ethnicity and religious affiliation.” [CCD 3.7]
 * <p>
 * The social history section shall contain a narrative description of the
 * person’s beliefs, home life, community life, work life, hobbies, and risky
 * habits.
 *
 * @see SocialHistory
 * @see SocialHistoryObservation
 * @see SmokingStatusObservation
 * @see TobaccoUse
 * @see PregnancyObservation
 * @see Client
 */
@Component("consol.SocialHistoryFactory")
public class SocialHistoryFactory extends OptionalTemplateFactory
        implements SectionFactory<SocialHistorySection, SocialHistory> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.17";

    @Value("${section.socialHistory.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    private static String buildSectionText(Collection<SocialHistory> socialHistory) {

        if (CollectionUtils.isEmpty(socialHistory)) {
            return "No known Social History.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Social History Element</th>" +
                "<th>Description</th>" +
                "<th>Effective Dates</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (SocialHistory socialHistoryEntry : socialHistory) {
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getSocialHistoryObservations())) {
                for (SocialHistoryObservation socialHistoryObservation : socialHistoryEntry
                        .getSocialHistoryObservations()) {
                    body.append("<tr>");

                    body.append("<td>");
                    if (socialHistoryObservation.getFreeText() != null) {
                        CcdUtils.addReferenceToSectionText(
                                SocialHistoryObservation.class.getSimpleName() + socialHistoryObservation.getId(),
                                socialHistoryObservation.getFreeText(), body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    if (socialHistoryObservation.getValue() != null
                            && socialHistoryObservation.getValue().getDisplayName() != null) {
                        body.append(StringEscapeUtils.escapeHtml(socialHistoryObservation.getValue().getDisplayName()));
                    } else if (socialHistoryObservation.getFreeTextValue() != null) {
                        body.append(StringEscapeUtils.escapeHtml(socialHistoryObservation.getFreeTextValue()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    CcdUtils.addEmptyCellToSectionText(body);
                    body.append("</td>");

                    body.append("</tr>");
                }
            }
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getPregnancyObservations())) {
                for (PregnancyObservation pregnancyObservation : socialHistoryEntry.getPregnancyObservations()) {
                    body.append("<tr>");

                    body.append("<td>" +
                            "Pregnant" +
                            "</td>");

                    body.append("<td>");
                    if (pregnancyObservation.getEstimatedDateOfDelivery() != null) {
                        body.append("Estimated Date of Delivery: ");
                        body.append(StringEscapeUtils.escapeHtml(
                                CcdUtils.formatTableDate(pregnancyObservation.getEstimatedDateOfDelivery())));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    CcdUtils.addDateCell(pregnancyObservation.getEffectiveTimeLow(), body);

                    body.append("</tr>");
                }
            }
            if (socialHistoryEntry.getSmokingStatusObservations() != null) {
                for (SmokingStatusObservation smokingStatusObservation : socialHistoryEntry
                        .getSmokingStatusObservations()) {
                    body.append("<tr>");

                    body.append("<td>" +
                            "Smoking status" +
                            "</td>");

                    body.append("<td>");
                    if (smokingStatusObservation.getValue() != null
                            && smokingStatusObservation.getValue().getDisplayName() != null) {
                        body.append(
                                StringEscapeUtils.escapeHtml(smokingStatusObservation.getValue().getDisplayName()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    CcdUtils.addDateCell(smokingStatusObservation.getEffectiveTimeLow(), body);

                    body.append("</tr>");
                }
            }
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getTobaccoUses())) {
                for (TobaccoUse tobaccoUse : socialHistoryEntry.getTobaccoUses()) {
                    body.append("<tr>");

                    body.append("<td>");
                    body.append("Tobacco Use");
                    body.append("</td>");

                    body.append("<td>");
                    if (tobaccoUse.getValue() != null && tobaccoUse.getValue().getDisplayName() != null) {
                        body.append(StringEscapeUtils.escapeHtml(tobaccoUse.getValue().getDisplayName()));
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("<td>");
                    if (tobaccoUse.getEffectiveTimeLow() != null) {
                        CcdUtils.addDateRangeToSectionText(tobaccoUse.getEffectiveTimeLow(), null, body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("</tr>");
                }
            }
        }

        if (body.length() == 0) {
            return "No known Social History.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public SocialHistorySection buildTemplateInstance(Collection<SocialHistory> socialHistory) {
        final SocialHistorySection section = ConsolFactory.eINSTANCE.createSocialHistorySection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        CE sectionCode = CcdUtils.createCE("29762-2", "Social History", CodeSystem.LOINC);
        section.setCode(sectionCode);

        ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Social History");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(socialHistory));

        if (CollectionUtils.isEmpty(socialHistory)) {
            // TODO buildNullSocialHistory() ?
            return section;
        }

        for (SocialHistory socialHistoryEntry : socialHistory) {
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getSocialHistoryObservations())) {
                for (SocialHistoryObservation socialHistoryObservation : socialHistoryEntry
                        .getSocialHistoryObservations()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(buildSocialHistoryObservation(socialHistoryObservation));
                    section.getEntries().add(entry);
                }
            }
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getPregnancyObservations())) {
                for (PregnancyObservation pregnancyObservation : socialHistoryEntry.getPregnancyObservations()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(buildPregnancyObservation(pregnancyObservation));
                    section.getEntries().add(entry);
                }
            }
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getSmokingStatusObservations())) {
                for (SmokingStatusObservation smokingStatusObservation : socialHistoryEntry
                        .getSmokingStatusObservations()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(buildSmokingStatusObservation(smokingStatusObservation));
                    section.getEntries().add(entry);
                }
            }
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getTobaccoUses())) {
                for (TobaccoUse tobaccoUse : socialHistoryEntry.getTobaccoUses()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(buildTobaccoUse(tobaccoUse));
                    section.getEntries().add(entry);
                }
            }
        }

        return section;
    }

    private Observation buildTobaccoUse(TobaccoUse tobaccoUse) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(SocialHistoryFactory.TemplateId.TOBACCO_USE, assertionCode);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(tobaccoUse.getEffectiveTimeLow(), null));

        //should be from ValueSet Tobacco Use 2.16.840.1.113883.11.20.9.41 DYNAMIC (CONF:16563, R2.0=CONF:1098-16563)
        //at least validate code system
        observation.getValues().add(CcdUtils.createCDOrTranslation(tobaccoUse.getValue(), CodeSystem.SNOMED_CT.getOid(), false));

        return observation;
    }

    private Observation buildSmokingStatusObservation(SmokingStatusObservation smokingStatusObservation) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(SocialHistoryFactory.TemplateId.SMOKING_STATUS, assertionCode);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(smokingStatusObservation.getEffectiveTimeLow(), null));

        if (observation.getEffectiveTime() == null) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
            low.setNullFlavor(NullFlavor.NI);
            effectiveTime.setLow(low);
            observation.setEffectiveTime(effectiveTime);
        }

        observation.getValues().add(CcdUtils.createCDFromValueSetOrTranslation(smokingStatusObservation.getValue(),
                ValueSetEnum.CURRENT_SMOKING_STATUS, false));

        return observation;
    }

    private Observation buildPregnancyObservation(PregnancyObservation pregnancyObservation) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(SocialHistoryFactory.TemplateId.PREGNANCY, assertionCode);

        CD value = CcdUtils.createCD("77386006", "Patient currently pregnant (finding)", CodeSystem.SNOMED_CT);
        observation.getValues().add(value);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(pregnancyObservation.getEffectiveTimeLow(), null));

        if (pregnancyObservation.getEstimatedDateOfDelivery() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            CD observationCode = CcdUtils.createCD("11778-8", "Delivery date", CodeSystem.LOINC);
            Observation observation1 = prebuildObservation(SocialHistoryFactory.TemplateId.ESTIMATED_DATE_OF_DELIVERY,
                    observationCode);

            TS value1 = DatatypesFactory.eINSTANCE.createTS();
            value1.setValue(CcdUtils.formatSimpleDate(pregnancyObservation.getEstimatedDateOfDelivery()));
            observation1.getValues().add(value1);

            entryRelationship.setObservation(observation1);
            observation.getEntryRelationships().add(entryRelationship);
        }

        return observation;
    }

    private Observation prebuildObservation(SocialHistoryFactory.TemplateId templateId, CD observationCode) {
        Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II ccdTemplateId = DatatypesFactory.eINSTANCE.createII();
        ccdTemplateId.setRoot(templateId.code);
        observation.getTemplateIds().add(ccdTemplateId);

        observation.setCode(observationCode);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        observation.setStatusCode(statusCode);

        return observation;
    }

    private Observation buildSocialHistoryObservation(SocialHistoryObservation socialHistoryObservation) {
        Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot(SocialHistoryFactory.TemplateId.SOCIAL_HISTORY_OBSERVATION.code);
        observation.getTemplateIds().add(templateId);

        observation.getIds().add(CcdUtils.getId(socialHistoryObservation.getId()));

        CD code = CcdUtils.createCD(socialHistoryObservation.getType());
        observation.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        observation.setStatusCode(statusCode);

        if (socialHistoryObservation.getFreeText() != null) {
            // non-null socialHistoryObservation.getFreeText() means that it was placed in
            // the section's free text block and can be referenced from here
            // [CONF-19222] The originalText, if present, SHOULD contain zero or one [0..1]
            // reference.
            // This reference/@value SHALL begin with a '#' and SHALL point to its
            // corresponding narrative
            // (using the approach defined in CDA Release 2, section 4.3.5.1)
            code.setOriginalText(CcdUtils.createReferenceEntryText(
                    SocialHistoryObservation.class.getSimpleName() + socialHistoryObservation.getId()));
        }

        if (socialHistoryObservation.getValue() != null) {
            // what code system?
            // @xsi:type="ANY"?
            // TODO [CONF‑8559] Observation/value can be any data type.
            // TODO Where Observation/value is a physical quantity, the unit of measure
            // SHALL be expressed using a valid Unified Code for Units of Measure (UCUM)
            // expression.
            observation.getValues().add(CcdUtils.createCD(socialHistoryObservation.getValue()));
        } else if (socialHistoryObservation.getFreeTextValue() != null) {
            observation.getValues().add(CcdUtils.createST(socialHistoryObservation.getFreeTextValue()));
        }

        return observation;
    }

    private enum TemplateId {
        /**
         * Represents a patient’s current smoking status.
         */
        SMOKING_STATUS("2.16.840.1.113883.10.20.22.4.78"),
        /**
         * Represents a patient’s current smoking status.<br/>
         *
         * @deprecated Smoking Status Observation templateId was changed on the Nov 29,
         * 2012 (SDWG call)
         */
        SMOKING_STATUS_OLD("2.16.840.1.113883.10.22.4.78"),
        /**
         * Represents a patient’s tobacco use.
         */
        TOBACCO_USE("2.16.840.1.113883.10.20.22.4.85"),
        /**
         * Represents current and/or prior pregnancy dates enabling investigators to
         * determine if the subject of the case report was pregnant during the course of
         * a condition.
         */
        PREGNANCY("2.16.840.1.113883.10.20.15.3.8"),
        /**
         * Defines the patient’s occupational, personal (e.g., lifestyle), social, and
         * environmental history and health risk factors, as well as administrative data
         * such as marital status, race, ethnicity, and religious affiliation.
         */
        SOCIAL_HISTORY_OBSERVATION("2.16.840.1.113883.10.20.22.4.38"),
        /**
         * Represents birth sex.<br/>
         * The 2015 Edition CEHRT requirements included a requirement to collect Birth
         * Sex. The {@code administrativeGenderCode} is not the appropriate place to
         * specify Birth Sex.
         */
        BIRTH_SEX("2.16.840.1.113883.10.20.22.4.200"),
        /**
         * Represents the anticipated date when a woman will give birth.
         */
        ESTIMATED_DATE_OF_DELIVERY("2.16.840.1.113883.10.20.15.3.1");

        public final String code;

        TemplateId(String code) {
            this.code = code;
        }

        @SuppressWarnings("unused")
        public static TemplateId getByCode(String code) {
            for (TemplateId id : SocialHistoryFactory.TemplateId.values()) {
                if (id.code.equalsIgnoreCase(code)) {
                    return id;
                }
            }
            return null;
        }
    }

}
