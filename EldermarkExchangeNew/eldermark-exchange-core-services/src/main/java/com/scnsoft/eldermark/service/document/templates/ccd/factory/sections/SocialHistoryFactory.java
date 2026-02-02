package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PregnancyObservation;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ActClassObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.SocialHistorySection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Social History</h1>
 * “This section contains data defining the patient’s occupational, personal (e.g. lifestyle), social,
 * and environmental history and health risk factors, as well as administrative data such as marital status,
 * race, ethnicity and religious affiliation.” [CCD 3.7]
 * <p>
 * The social history section shall contain a narrative description of the person’s beliefs, home life,
 * community life, work life, hobbies, and risky habits.
 *
 * @see SocialHistory
 * @see SocialHistoryObservation
 * @see SmokingStatusObservation
 * @see TobaccoUse
 * @see PregnancyObservation
 * @see Client
 */
@Component
public class SocialHistoryFactory extends OptionalTemplateFactory implements ParsableSectionFactory<SocialHistorySection, SocialHistory> {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    private static final Logger logger = LoggerFactory.getLogger(SocialHistoryFactory.class);

    @Value("${section.socialHistory.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    private enum TemplateId {
        /**
         * Social History observation.<br/>
         * ASTM/HL7 Continuity of Care Document template. In this template the {@code <code>} element identifies the type social history observation.
         */
        SOCIAL_HISTORY_OBSERVATION("2.16.840.1.113883.10.20.1.33");

        public final String code;

        TemplateId(String code) {
            this.code = code;
        }

        public static TemplateId getByCode(String code) {
            for (TemplateId id : SocialHistoryFactory.TemplateId.values()) {
                if (id.code.equalsIgnoreCase(code)) {
                    return id;
                }
            }
            return null;
        }
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

    private Observation buildTobaccoUse(TobaccoUse tobaccoUse) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(TemplateId.SOCIAL_HISTORY_OBSERVATION, assertionCode);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(tobaccoUse.getEffectiveTimeLow(), null));

        //should be from ValueSet Tobacco Use 2.16.840.1.113883.11.20.9.41 DYNAMIC (CONF:16563, R2.0=CONF:1098-16563)
        //at least validate code system
        observation.getValues().add(CcdUtils.createCDOrTranslation(tobaccoUse.getValue(), CodeSystem.SNOMED_CT.getOid(), false));

        return observation;
    }

    private Observation buildSmokingStatusObservation(SmokingStatusObservation smokingStatusObservation) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(TemplateId.SOCIAL_HISTORY_OBSERVATION, assertionCode);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(smokingStatusObservation.getEffectiveTimeLow(), null));

        observation.getValues().add(CcdUtils.createCDFromValueSetOrTranslation(smokingStatusObservation.getValue(),
                ValueSetEnum.CURRENT_SMOKING_STATUS, false));

        return observation;
    }

    private Observation buildPregnancyObservation(PregnancyObservation pregnancyObservation) {
        CD assertionCode = CcdUtils.createCD("ASSERTION", null, CodeSystem.HL7_ACT_CODE);
        Observation observation = prebuildObservation(TemplateId.SOCIAL_HISTORY_OBSERVATION, assertionCode);

        CD value = CcdUtils.createCD("77386006", "Patient currently pregnant (finding)", CodeSystem.SNOMED_CT);
        observation.getValues().add(value);

        observation.setEffectiveTime(CcdUtils.convertEffectiveTime(pregnancyObservation.getEffectiveTimeLow(), null));

        if (pregnancyObservation.getEstimatedDateOfDelivery() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            // TODO what is the correct way to represent Pregnancy Observation > Estimated Delivery Date in HL7 CCD?
            CD observationCode = CcdUtils.createCD("11778-8", "Delivery date", CodeSystem.LOINC);
            Observation observation1 = prebuildObservation(TemplateId.SOCIAL_HISTORY_OBSERVATION, observationCode);

            TS value1 = DatatypesFactory.eINSTANCE.createTS();
            value1.setValue(CcdUtils.formatSimpleDate(pregnancyObservation.getEstimatedDateOfDelivery()));
            observation1.getValues().add(value1);

            entryRelationship.setObservation(observation1);
            observation.getEntryRelationships().add(entryRelationship);
        }

        return observation;
    }

    private Observation prebuildObservation(TemplateId templateId, CD observationCode) {
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
        templateId.setRoot(TemplateId.SOCIAL_HISTORY_OBSERVATION.code);
        observation.getTemplateIds().add(templateId);

        observation.getIds().add(CcdUtils.getId(socialHistoryObservation.getId()));

        CD code = CcdUtils.createCD(socialHistoryObservation.getType());
        observation.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        observation.setStatusCode(statusCode);

        if (socialHistoryObservation.getFreeText() != null) {
            // non-null socialHistoryObservation.getFreeText() means that it was placed in the section's free text block and can be referenced from here
            // [CONF-19222] The originalText, if present, SHOULD contain zero or one [0..1] reference.
            // This reference/@value SHALL begin with a '#' and SHALL point to its corresponding narrative
            // (using the approach defined in CDA Release 2, section 4.3.5.1)
            code.setOriginalText(CcdUtils.createReferenceEntryText(SocialHistoryObservation.class.getSimpleName()
                    + socialHistoryObservation.getId()));
        }

        if (socialHistoryObservation.getValue() != null) {
            // what code system?
            // @xsi:type="ANY"?
            // TODO [CONF‑8559] Observation/value can be any data type.
            // TODO Where Observation/value is a physical quantity, the unit of measure SHALL be expressed using a valid Unified Code for Units of Measure (UCUM) expression.
            observation.getValues().add(CcdUtils.createCD(socialHistoryObservation.getValue()));
        } else if (socialHistoryObservation.getFreeTextValue() != null) {
            observation.getValues().add(CcdUtils.createST(socialHistoryObservation.getFreeTextValue()));
        }

        return observation;
    }

    @Override
    public SocialHistorySection buildTemplateInstance(Collection<SocialHistory> socialHistory) {
        final SocialHistorySection section = CCDFactory.eINSTANCE.createSocialHistorySection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.15"));

        CE sectionCode = CcdUtils.createCE("29762-2", "Social History", CodeSystem.LOINC);
        section.setCode(sectionCode);

        ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Social History");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(socialHistory));

        for (SocialHistory socialHistoryEntry : socialHistory) {
            if (CollectionUtils.isNotEmpty(socialHistoryEntry.getSocialHistoryObservations())) {
                for (SocialHistoryObservation socialHistoryObservation : socialHistoryEntry.getSocialHistoryObservations()) {
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
                for (SmokingStatusObservation smokingStatusObservation : socialHistoryEntry.getSmokingStatusObservations()) {
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

    /**
     * The {@code <value>} element reports the value associated with the social history observation.
     * The data type to use for each observation should be drawn from the table below.
     * <table border = "1">
     * <caption>Social History Codes</caption>
     * <tbody>
     * <tr>
     * <th>Code</th>
     * <th>Description</th>
     * <th>Data Type</th>
     * <th>Units</th>
     * </tr>
     * <tr>
     * <td>229819007</td>
     * <td>Smoking</td>
     * <td rowspan="3" align="center">PQ</td>
     * <td>{pack}/d or {pack}/wk or {pack}/a</td>
     * </tr>
     * <tr>
     * <td>256235009</td>
     * <td>Exercise</td>
     * <td>{times}/wk</td>
     * </tr>
     * <tr>
     * <td>160573003</td>
     * <td>ETOH (Alcohol) Use</td>
     * <td>{drink}/d or {drink}/wk</td>
     * </tr>
     * <tr>
     * <td>364393001</td>
     * <td>Diet</td>
     * <td rowspan="4" align="center">CD</td>
     * <td rowspan="5">
     * <center>N/A</center>
     * </td>
     * </tr>
     * <tr>
     * <td>364703007</td>
     * <td>Employment</td>
     * </tr>
     * <tr>
     * <td>425400000</td>
     * <td>Toxic Exposure</td>
     * </tr>
     * <tr>
     * <td>363908000</td>
     * <td>Drug Use</td>
     * </tr>
     * <tr>
     * <td>228272008</td>
     * <td>Other Social History</td>
     * <td align="center">ANY</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @see com.scnsoft.eldermark.service.document.templates.consol.parser.sections.SocialHistoryParser
     */
    private SocialHistoryObservation parseSocialHistoryObservationWithST(org.openhealthtools.mdht.uml.cda.ccd.SocialHistoryObservation ccdObservation, Client client) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(client);

        SocialHistoryObservation socialHistoryObservation = new SocialHistoryObservation();
        socialHistoryObservation.setOrganization(client.getOrganization());

        CD code = ccdObservation.getCode();
        // (CONF:8555) Observation/value can be any data type.
        final ANY observationValueANY = ObservationFactory.getValue(ccdObservation, ANY.class);
        final ST observationValueST = observationValueANY instanceof ST ? (ST) observationValueANY : null;
        final CD observationValueCD = observationValueANY instanceof CD ? (CD) observationValueANY : null;
        if (CcdParseUtils.hasContent(observationValueCD)) {
            socialHistoryObservation.setValue(ccdCodeFactory.convert(observationValueCD));
        }
        if (code != null) {
            socialHistoryObservation.setType(ccdCodeFactory.convert(code));
            socialHistoryObservation.setFreeText(CcdTransform.EDtoString(code.getOriginalText(), socialHistoryObservation.getValue()));
            if (CcdParseUtils.hasContent(observationValueST)) {
                socialHistoryObservation.setFreeTextValue(observationValueST.getText());
            }
        }
        // TODO add support for PQ
        final PQ observationValuePQ = observationValueANY instanceof PQ ? (PQ) observationValueANY : null;
        if (CcdParseUtils.hasContent(observationValuePQ)) {
            logger.warn("DATA LOSS: Observation value of type PQ is not persisted.");
        }

        return socialHistoryObservation;
    }

    @Override
    public List<SocialHistory> parseSection(Client client, SocialHistorySection socialHistorySection) {
        if (!CcdParseUtils.hasContent(socialHistorySection) || CollectionUtils.isEmpty(socialHistorySection.getSocialHistoryObservations())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final SocialHistory socialHistory = new SocialHistory();
        socialHistory.setOrganization(client.getOrganization());
        socialHistory.setClient(client);
        II ii = socialHistorySection.getId();
        if (ii != null && ii.getExtension() != null) {
            socialHistory.setLegacyId(Long.parseLong(ii.getExtension()));
        } else {
            socialHistory.setLegacyId(0L);
        }

        // Can not get TobaccoUse, SmokingStatusObservation, and PregnancyObservation from HL7 CCD -> I suppose that these types of observation are encoded as a general SocialHistoryObservation
        // TODO test on real examples
        final List<TobaccoUse> tobaccoUses = null;
        final List<SmokingStatusObservation> smokingStatusObservations = null;
        final List<PregnancyObservation> pregnancyObservations = null;
        final List<SocialHistoryObservation> socialHistoryObservations = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.ccd.SocialHistoryObservation ccdObservation : socialHistorySection.getSocialHistoryObservations()) {
            final SocialHistoryObservation socialHistoryObservation = parseSocialHistoryObservationWithST(ccdObservation, client);
            if (socialHistoryObservation != null) {
                socialHistoryObservation.setSocialHistory(socialHistory);
                socialHistoryObservations.add(socialHistoryObservation);
            }
        }
        socialHistory.setTobaccoUses(tobaccoUses);
        socialHistory.setSmokingStatusObservations(smokingStatusObservations);
        socialHistory.setPregnancyObservations(pregnancyObservations);
        socialHistory.setSocialHistoryObservations(socialHistoryObservations);

        return new ArrayList<>(Collections.singleton(socialHistory));
    }

}
