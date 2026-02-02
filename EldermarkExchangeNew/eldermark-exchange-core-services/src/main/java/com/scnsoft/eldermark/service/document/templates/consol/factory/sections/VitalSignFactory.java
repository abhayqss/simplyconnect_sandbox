package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsOrganizer;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsSectionEntriesOptional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

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
@Component("consol.VitalSignFactory")
public class VitalSignFactory extends OptionalTemplateFactory
        implements SectionFactory<VitalSignsSectionEntriesOptional, VitalSign> {

    // private static final String LEGACY_TABLE = "VitalSigns_NWHIN";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.4.1";
    private static final String TEMPLATE_ID_VITAL_SIGN_OBSERVATION = "2.16.840.1.113883.10.20.22.4.27";

    private static final String IDSTR = "vit";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

    @Value("${section.vitalSigns.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
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
    public VitalSignsSectionEntriesOptional buildTemplateInstance(Collection<VitalSign> vitalSigns) {
        final VitalSignsSectionEntriesOptional vitalSignsSection = ConsolFactory.eINSTANCE
                .createVitalSignsSectionEntriesOptional();
        vitalSignsSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        CE sectionCode = CcdUtils.createCE("8716-3", "Vital Signs", LOINC);
        vitalSignsSection.setCode(sectionCode);

        vitalSignsSection.setTitle(DatatypesFactory.eINSTANCE.createST("Vital Signs"));

        vitalSignsSection.createStrucDocText(buildSectionText(vitalSigns));

        if (CollectionUtils.isEmpty(vitalSigns)) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setOrganizer(buildNullVitalSignsOrganizer());
            vitalSignsSection.getEntries().add(entry);
            return vitalSignsSection;
        }

        for (VitalSign vitalSign : vitalSigns) {
            //todo - deduplicate with ccd package VitalSignFactory?
            VitalSignsOrganizer vitalSignsOrganizer = ConsolFactory.eINSTANCE.createVitalSignsOrganizer();
            vitalSignsSection.addOrganizer(vitalSignsOrganizer);

            vitalSignsOrganizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
            vitalSignsOrganizer.setMoodCode(ActMood.EVN);

            vitalSignsOrganizer.getTemplateIds()
                    .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.26"));

            vitalSignsOrganizer.getIds().add(CcdUtils.getId(vitalSign.getId()));

            CE code = CcdUtils.createCE("46680005", "Vital Signs", CodeSystem.SNOMED_CT);
            vitalSignsOrganizer.setCode(code);

            vitalSignsOrganizer.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            vitalSignsOrganizer.setEffectiveTime(CcdUtils.convertEffectiveTime(vitalSign.getEffectiveTime()));

            List<VitalSignObservation> vitalSignObservations = vitalSign.getVitalSignObservations();
            if (!CollectionUtils.isEmpty(vitalSignObservations)) {
                for (VitalSignObservation vitalSignObservation : vitalSignObservations) {
                    var ccdObservation = ConsolFactory.eINSTANCE.createVitalSignObservation();
                    vitalSignsOrganizer.addObservation(ccdObservation);
                    ccdObservation.setClassCode(ActClassObservation.OBS);
                    ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

                    ccdObservation.getTemplateIds()
                            .add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_VITAL_SIGN_OBSERVATION));

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
                        String unit = null;
                        if (vitalSignObservation.getUnit() != null) {
                            unit = vitalSignObservation.getUnit()
                                    .replace("]", "")
                                    .replace("[", "")
                                    .replace(" ", "");
                        }
                        if (StringUtils.isNotEmpty(unit)) {
                            pq.setUnit(unit);
                        } else {
                            pq.setUnit(null);
                        }
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
                        Author author = consolSectionEntryFactory.buildAuthor(vitalSignObservation.getAuthor());
                        ccdObservation.getAuthors().add(author);
                    }
                }
            } else {
                vitalSignsOrganizer.addObservation(buildNullVitalSignsObservation());
            }
        }

        return vitalSignsSection;
    }

    public VitalSignsOrganizer buildNullVitalSignsOrganizer() {
        //todo [ccd] review below
        var organizer = ConsolFactory.eINSTANCE.createVitalSignsOrganizer();

        // Read the above comment for @classCode
        organizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
        organizer.setMoodCode(ActMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.26");
        organizer.getTemplateIds().add(templateId);

        organizer.getIds().add(CcdUtils.getNullId());

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("46680005");
        code.setCodeSystem("2.16.840.1.113883.6.96");
        code.setCodeSystemName("SNOMED -CT");
        code.setDisplayName("Vital signs");

        organizer.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();

        statusCode.setCode("completed");
        organizer.setStatusCode(statusCode);
        organizer.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        organizer.addObservation(buildNullVitalSignsObservation());

        return organizer;
    }

    public org.openhealthtools.mdht.uml.cda.consol.VitalSignObservation buildNullVitalSignsObservation() {
        var o = ConsolFactory.eINSTANCE.createVitalSignObservation();

        o.setClassCode(ActClassObservation.OBS);
        o.setMoodCode(x_ActMoodDocumentObservation.EVN);

        o.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_VITAL_SIGN_OBSERVATION));

        o.getIds().add(CcdUtils.getNullId());

        o.setCode(CcdUtils.createNillCode());

        o.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        o.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        PQ pq = DatatypesFactory.eINSTANCE.createPQ();
        pq.setNullFlavor(NullFlavor.NI);
        o.getValues().add(pq);

        return o;
    }

}
