package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import org.eclipse.mdht.uml.cda.Author;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsOrganizer;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.CodeSystem.LOINC;

/**
 * <h1>Vital Signs</h1>
 * “The section may contain all vital signs for the period of time being summarized, but at a minimum
 * should include notable vital signs such as the most recent, maximum and/or minimum, or both, baseline,
 * or relevant trends.” [CCD 3.12]
 *
 * @see VitalSign
 * @see VitalSignObservation
 * @see Resident
 */
@Component
public class VitalSignFactory extends OptionalTemplateFactory implements ParsableSectionFactory<VitalSignsSection, VitalSign> {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final Logger logger = LoggerFactory.getLogger(VitalSignFactory.class);
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

            vitalSignsOrganizer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.35"));

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

                    ccdObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_RESULT_OBSERVATION));

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

                    ccdObservation.setEffectiveTime(CcdUtils.convertEffectiveTime(vitalSignObservation.getEffectiveTime()));

                    PQ pq = DatatypesFactory.eINSTANCE.createPQ();
                    if (vitalSignObservation.getValue() != null) {
                        pq.setValue(vitalSignObservation.getValue());
                        pq.setUnit(vitalSignObservation.getUnit());
                    } else {
                        pq.setNullFlavor(NullFlavor.NI);
                    }
                    ccdObservation.getValues().add(pq);

                    if (vitalSignObservation.getInterpretationCode() != null) {
                        ccdObservation.getInterpretationCodes().add(CcdUtils.createCE(vitalSignObservation.getInterpretationCode()));
                    }


                    if (vitalSignObservation.getMethodCode() != null) {
                        ccdObservation.getMethodCodes().add(CcdUtils.createCE(vitalSignObservation.getMethodCode()));
                    }

                    if (vitalSignObservation.getTargetSiteCode() != null) {
                        ccdObservation.getTargetSiteCodes().add(CcdUtils.createCE(vitalSignObservation.getTargetSiteCode()));
                    }

                    if (vitalSignObservation.getAuthor() != null) {
                        Author author = SectionEntryFactory.buildAuthor(vitalSignObservation.getAuthor());
                        ccdObservation.getAuthors().add(author);
                    }
                }
            } else {
                Observation ccdObservation = CCDFactory.eINSTANCE.createResultObservation();
                vitalSignsOrganizer.addObservation(ccdObservation);
                ccdObservation.setClassCode(ActClassObservation.OBS);
                ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

                ccdObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_RESULT_OBSERVATION));

                ccdObservation.getIds().add(CcdUtils.getNullId());

                ccdObservation.setCode(CcdUtils.createNillCode());

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

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Date</th>");
        sectionText.append("<th>Type</th>");
        sectionText.append("<th>Value</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (VitalSign vitalSign : vitalSignList) {
            if (vitalSign.getVitalSignObservations() != null) {
                for (VitalSignObservation vitalSignObservation : vitalSign.getVitalSignObservations()) {
                    sectionText.append("<tr>");

                    CcdUtils.addDateCell(vitalSignObservation.getEffectiveTime(),sectionText);
                    CcdUtils.addCellToSectionText(vitalSignObservation.getResultTypeCode(), sectionText);
                    //CcdUtils.addCellToSectionText(vitalSignObservation.getValue().toString(), sectionText);

                    sectionText.append("<td>");
                    if(vitalSignObservation.getValue() != null) {
                        String value = vitalSignObservation.getValue().toString();
                        if (!StringUtils.isEmpty(vitalSignObservation.getUnit())) {
                            String unit = vitalSignObservation.getUnit().replace("]","").replace("[","");
                            value = value + " " + unit;
                        }
                        CcdUtils.addReferenceToSectionText(IDSTR + vitalSignObservation.getId(), value, sectionText);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");

                    sectionText.append("</tr>");
                }
            }
        }
        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public List<VitalSign> parseSection(Resident resident, VitalSignsSection vitalSignsSection) {
        if (!CcdParseUtils.hasContent(vitalSignsSection)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<VitalSign> vitalSigns = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vitalSignsSection.getVitalSignsOrganizers())) {
            for (VitalSignsOrganizer vitalSignsOrganizer : vitalSignsSection.getVitalSignsOrganizers()) {
                VitalSign vitalSign = new VitalSign();
                // TODO: inbound ID type is String
                vitalSign.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(vitalSignsOrganizer.getIds()));
                vitalSign.setResident(resident);
                vitalSign.setDatabase(resident.getDatabase());
                IVL_TS effectiveTime = vitalSignsOrganizer.getEffectiveTime();
                vitalSign.setEffectiveTime(CcdParseUtils.convertTsToDate(effectiveTime));
                List<VitalSignObservation> vitalSignObservations = new ArrayList<>();
                if (!CollectionUtils.isEmpty(vitalSignsOrganizer.getObservations())) {
                    for (Observation ccdObservation : vitalSignsOrganizer.getObservations()) {
                        VitalSignObservation vitalSignObservation = new VitalSignObservation();
                        vitalSignObservation.setVitalSign(vitalSign);
                        vitalSignObservation.setDatabase(resident.getDatabase());
                        vitalSignObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdObservation.getIds()));

                        vitalSignObservation.setResultTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));

                        IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
                        vitalSignObservation.setEffectiveTime(CcdParseUtils.convertTsToDate(observationEffectiveTime));

                        if (!CollectionUtils.isEmpty(ccdObservation.getValues())) {
                            ANY any = ccdObservation.getValues().get(0);
                            if (any instanceof PQ) {
                                PQ pq = (PQ)any;
                                vitalSignObservation.setUnit(pq.getUnit());
                                vitalSignObservation.setValue(pq.getValue() != null ? pq.getValue().doubleValue() : null);
                            }
                        }

                        if (!CollectionUtils.isEmpty(ccdObservation.getInterpretationCodes())) {
                            vitalSignObservation.setInterpretationCode(ccdCodeFactory.convert(ccdObservation.getInterpretationCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getMethodCodes())) {
                            vitalSignObservation.setMethodCode(ccdCodeFactory.convert(ccdObservation.getMethodCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getTargetSiteCodes())) {
                            vitalSignObservation.setTargetSiteCode(ccdCodeFactory.convert(ccdObservation.getTargetSiteCodes().get(0)));
                        }
                        if (!CollectionUtils.isEmpty(ccdObservation.getAuthors())) {
                            vitalSignObservation.setAuthor(sectionEntryParseFactory.parseAuthor(ccdObservation.getAuthors().get(0), resident, "VitalSigns_NWHIN"));
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
