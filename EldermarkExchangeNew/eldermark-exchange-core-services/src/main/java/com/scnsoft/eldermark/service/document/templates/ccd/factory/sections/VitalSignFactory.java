package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.parser.entries.SectionEntryParseFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.Author;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsOrganizer;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.document.ccd.CodeSystem.LOINC;

/**
 * <h1>Vital Signs</h1> “The section may contain all vital signs for the period
 * of time being summarized, but at a minimum should include notable vital signs
 * such as the most recent, maximum and/or minimum, or both, baseline, or
 * relevant trends.” [CCD 3.12]
 *
 * @see VitalSign
 * @see VitalSignObservation
 * @see Client
 */
@Component
public class VitalSignFactory extends OptionalTemplateFactory
        implements ParsableSectionFactory<VitalSignsSection, VitalSign> {

    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    //why result observation? mb there is specific tempmlateId for HL7 CCD vital sign observation
    private static final String TEMPLATE_ID_RESULT_OBSERVATION = "2.16.840.1.113883.10.20.1.31";
    private static final String IDSTR = "vit";

    @Value("${section.vitalSigns.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public VitalSignsSection buildTemplateInstance(Collection<VitalSign> vitalSigns) {
        final VitalSignsSection vitalSignsSection = CCDFactory.eINSTANCE.createVitalSignsSection();
        vitalSignsSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.16"));

        CE sectionCode = CcdUtils.createCE("8716-3", "Vital Signs", LOINC);
        vitalSignsSection.setCode(sectionCode);

        vitalSignsSection.setTitle(DatatypesFactory.eINSTANCE.createST("Vital Signs"));

        vitalSignsSection.createStrucDocText(buildSectionText(vitalSigns));

        for (VitalSign vitalSign : vitalSigns) {
            VitalSignsOrganizer vitalSignsOrganizer = CCDFactory.eINSTANCE.createVitalSignsOrganizer();
            vitalSignsSection.addOrganizer(vitalSignsOrganizer);

            vitalSignsOrganizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
            vitalSignsOrganizer.setMoodCode(ActMood.EVN);

            vitalSignsOrganizer.getTemplateIds()
                    .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.35"));

            vitalSignsOrganizer.getIds().add(CcdUtils.getId(vitalSign.getId()));

            CE code = CcdUtils.createCE("46680005", "Vital Signs", CodeSystem.SNOMED_CT);
            vitalSignsOrganizer.setCode(code);

            vitalSignsOrganizer.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            vitalSignsOrganizer.setEffectiveTime(CcdUtils.convertEffectiveTime(vitalSign.getEffectiveTime()));

            List<VitalSignObservation> vitalSignObservations = vitalSign.getVitalSignObservations();
            if (!CollectionUtils.isEmpty(vitalSignObservations)) {
                for (VitalSignObservation vitalSignObservation : vitalSignObservations) {
                    Observation ccdObservation = CCDFactory.eINSTANCE.createResultObservation();
                    vitalSignsOrganizer.addObservation(ccdObservation);
                    ccdObservation.setClassCode(ActClassObservation.OBS);
                    ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

                    ccdObservation.getTemplateIds()
                            .add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_RESULT_OBSERVATION));

                    ccdObservation.getIds().add(CcdUtils.getId(vitalSignObservation.getId()));

                    ccdObservation.setCode(CcdUtils.createCE(vitalSignObservation.getResultTypeCode(), LOINC.getOid()));

                    if (vitalSignObservation.getValue() != null) {
                        String refId = IDSTR + vitalSignObservation.getId();
                        ED originalText = DatatypesFactory.eINSTANCE.createED();
                        TEL ref = DatatypesFactory.eINSTANCE.createTEL("#" + refId);
                        originalText.setReference(ref);
                        ccdObservation.setText(originalText);
                    }

                    ccdObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

                    ccdObservation
                            .setEffectiveTime(CcdUtils.convertEffectiveTime(vitalSignObservation.getEffectiveTime()));

                    PQ pq = DatatypesFactory.eINSTANCE.createPQ();
                    if (vitalSignObservation.getValue() != null) {
                        pq.setValue(vitalSignObservation.getValue());
                        pq.setUnit(vitalSignObservation.getUnit());
                    } else {
                        pq.setNullFlavor(NullFlavor.NI);
                    }
                    ccdObservation.getValues().add(pq);

                    if (vitalSignObservation.getInterpretationCode() != null) {
                        ccdObservation.getInterpretationCodes()
                                .add(CcdUtils.createCE(vitalSignObservation.getInterpretationCode()));
                    }

                    if (vitalSignObservation.getMethodCode() != null) {
                        ccdObservation.getMethodCodes().add(CcdUtils.createCE(vitalSignObservation.getMethodCode()));
                    }

                    if (vitalSignObservation.getTargetSiteCode() != null) {
                        ccdObservation.getTargetSiteCodes()
                                .add(CcdUtils.createCE(vitalSignObservation.getTargetSiteCode()));
                    }

                    if (vitalSignObservation.getAuthor() != null) {
                        Author author = ccdSectionEntryFactory.buildAuthor(vitalSignObservation.getAuthor());
                        ccdObservation.getAuthors().add(author);
                    }
                }
            } else {
                Observation ccdObservation = CCDFactory.eINSTANCE.createResultObservation();
                vitalSignsOrganizer.addObservation(ccdObservation);
                ccdObservation.setClassCode(ActClassObservation.OBS);
                ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

                ccdObservation.getTemplateIds()
                        .add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_RESULT_OBSERVATION));

                ccdObservation.getIds().add(CcdUtils.getNullId());

                ccdObservation.setCode(CcdUtils.createNillCode());

                ccdObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

                ccdObservation.setEffectiveTime(CcdUtils.getNullEffectiveTime());

                PQ pq = DatatypesFactory.eINSTANCE.createPQ();
                pq.setNullFlavor(NullFlavor.NI);
                ccdObservation.getValues().add(pq);
            }
        }

        return vitalSignsSection;
    }

    private static String buildSectionText(Collection<VitalSign> vitalSignList) {

        if (CollectionUtils.isEmpty(vitalSignList)) {
            return "No known vital signs.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Date</th>" +
                "<th>Type</th>" +
                "<th>Value</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (VitalSign vitalSign : vitalSignList) {
            if (CollectionUtils.isNotEmpty(vitalSign.getVitalSignObservations())) {
                for (VitalSignObservation vitalSignObservation : vitalSign.getVitalSignObservations()) {
                    body.append("<tr>");

                    CcdUtils.addDateCell(vitalSignObservation.getEffectiveTime(), body);
                    CcdUtils.addCellToSectionText(vitalSignObservation.getResultTypeCode(), body);

                    body.append("<td>");
                    if (vitalSignObservation.getValue() != null) {
                        String value = vitalSignObservation.getValue().toString();
                        if (StringUtils.isNotEmpty(vitalSignObservation.getUnit())) {
                            String unit = vitalSignObservation.getUnit()
                                    .replace("]", "")
                                    .replace("[", "");
                            //.replace(" ", "");
                            value = value + " " + unit;
                        }
                        //todo - why IDSTR instead of .class simplyName?
                        CcdUtils.addReferenceToSectionText(IDSTR + vitalSignObservation.getId(), value, body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");

                    body.append("</tr>");
                }
            }
        }

        if (body.length() == 0) {
            return "No known vital signs.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public List<VitalSign> parseSection(Client client, VitalSignsSection vitalSignsSection) {
        if (!CcdParseUtils.hasContent(vitalSignsSection)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<VitalSign> vitalSigns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vitalSignsSection.getVitalSignsOrganizers())) {
            for (VitalSignsOrganizer vitalSignsOrganizer : vitalSignsSection.getVitalSignsOrganizers()) {
                VitalSign vitalSign = new VitalSign();
                // TODO: inbound ID type is String
                vitalSign.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(vitalSignsOrganizer.getIds()));
                vitalSign.setClient(client);
                vitalSign.setOrganization(client.getOrganization());
                IVL_TS effectiveTime = vitalSignsOrganizer.getEffectiveTime();
                vitalSign.setEffectiveTime(CcdParseUtils.convertTsToDate(effectiveTime));
                List<VitalSignObservation> vitalSignObservations = new ArrayList<>();
                if (!CollectionUtils.isEmpty(vitalSignsOrganizer.getObservations())) {
                    for (Observation ccdObservation : vitalSignsOrganizer.getObservations()) {
                        VitalSignObservation vitalSignObservation = new VitalSignObservation();
                        vitalSignObservation.setVitalSign(vitalSign);
                        vitalSignObservation.setOrganization(client.getOrganization());
                        vitalSignObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdObservation.getIds()));

                        vitalSignObservation.setResultTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));

                        IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
                        vitalSignObservation.setEffectiveTime(CcdParseUtils.convertTsToDate(observationEffectiveTime));

                        if (!CollectionUtils.isEmpty(ccdObservation.getValues())) {
                            ANY any = ccdObservation.getValues().get(0);
                            if (any instanceof PQ) {
                                PQ pq = (PQ) any;
                                vitalSignObservation.setUnit(pq.getUnit());
                                vitalSignObservation
                                        .setValue(pq.getValue() != null ? pq.getValue().doubleValue() : null);
                            }
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getInterpretationCodes())) {
                            vitalSignObservation.setInterpretationCode(
                                    ccdCodeFactory.convert(ccdObservation.getInterpretationCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getMethodCodes())) {
                            vitalSignObservation
                                    .setMethodCode(ccdCodeFactory.convert(ccdObservation.getMethodCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getTargetSiteCodes())) {
                            vitalSignObservation.setTargetSiteCode(
                                    ccdCodeFactory.convert(ccdObservation.getTargetSiteCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getAuthors())) {
                            vitalSignObservation.setAuthor(sectionEntryParseFactory
                                    .parseAuthor(ccdObservation.getAuthors().get(0), client, "VitalSigns_NWHIN"));
                        }
                        vitalSignObservations.add(vitalSignObservation);
                    }
                    vitalSign.setVitalSignObservations(vitalSignObservations);
                }

                vitalSigns.add(vitalSign);
            }
        }

        return vitalSigns;
    }
}
