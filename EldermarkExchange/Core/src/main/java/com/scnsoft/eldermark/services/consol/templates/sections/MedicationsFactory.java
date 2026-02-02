package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <h1>Medications</h1>
 * “The Medications section defines a patient’s current medications and pertinent medication history.” [CCD 3.9]
 *
 * @see Medication
 * @see MedicationInformation
 * @see Instructions
 * @see Indication
 * @see Resident
 */
@Component("consol.MedicationsFactory")
public class MedicationsFactory extends RequiredTemplateFactory implements SectionFactory<MedicationsSection, Medication> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.1.1";

    @Override
    public MedicationsSection buildTemplateInstance(Collection<Medication> medications) {
        final MedicationsSection section = ConsolFactory.eINSTANCE.createMedicationsSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("10160-0", "HISTORY OF MEDICATION USE", CodeSystem.LOINC);
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Medications");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(medications));

        if (CollectionUtils.isEmpty(medications)) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setSubstanceAdministration(SectionEntryFactory.buildNullMedicationActivity());
            section.getEntries().add(entry);
            return section;
        }

        for (Medication medication : medications) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();

            Set<Class> entriesReferredToSectionText = new HashSet<>();
            entriesReferredToSectionText.add(MedicationInformation.class);
            entriesReferredToSectionText.add(Medication.class);
            entriesReferredToSectionText.add(Instructions.class);

            entry.setSubstanceAdministration(SectionEntryFactory.buildMedicationActivity(medication, entriesReferredToSectionText));
            section.getEntries().add(entry);
        }

        return section;
    }

    private static String buildSectionText(Collection<Medication> medications) {

        if (CollectionUtils.isEmpty(medications)) {
            return "No known medications.";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Medication</th>");
        sectionText.append("<th>Directions</th>");
        sectionText.append("<th>Dates</th>");
        sectionText.append("<th>Status</th>");
        sectionText.append("<th>Indications</th>");
        sectionText.append("<th>Fill Instructions</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Medication medication : medications) {
            sectionText.append("<tr>");

            sectionText.append("<td>");
            MedicationInformation medInformation = medication.getMedicationInformation();
            if (medInformation != null) {
                String productName = medInformation.getProductNameText();
                if (productName != null) {
                    CcdUtils.addReferenceToSectionText(MedicationInformation.class.getSimpleName() + medInformation.getId(), productName, sectionText);
                } else {
                    CcdUtils.addEmptyCellToSectionText(sectionText);
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("<td>");
            if (medication.getFreeTextSig() != null) {
                CcdUtils.addReferenceToSectionText(Medication.class.getSimpleName() + medication.getId(), medication.getFreeTextSig(), sectionText);
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("<td>");
            if (medication.getMedicationStarted() != null || medication.getMedicationStopped() != null) {
                CcdUtils.addDateRangeToSectionText(medication.getMedicationStarted(), medication.getMedicationStopped(), sectionText);
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("<td>");
            if (medication.getStatusCode() != null) {
                sectionText.append(StringEscapeUtils.escapeHtml4(medication.getStatusCode()));
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("<td>");
            List<Indication> indications = medication.getIndications();
            if (indications != null && !indications.isEmpty()) {
                String prefix = "";
                for (Indication indication : indications) {
                    if (indication.getValue() != null && indication.getValue().getDisplayName() != null) {
                        sectionText.append(prefix);
                        sectionText.append(StringEscapeUtils.escapeHtml4(indication.getValue().getDisplayName()));
                        prefix = "; ";
                    }
                }
            } else if (medication.getMedicationReport() != null && StringUtils.isNotBlank(medication.getMedicationReport().getIndicatedFor())) {
                sectionText.append(medication.getMedicationReport().getIndicatedFor());
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("<td>");
            Instructions instructions = medication.getInstructions();
            if (instructions != null) {
                if (instructions.getText() != null) {
                    CcdUtils.addReferenceToSectionText(Instructions.class.getSimpleName() + instructions.getId(), instructions.getText(), sectionText);
                } else {
                    CcdUtils.addEmptyCellToSectionText(sectionText);
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            sectionText.append("</tr>");
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

}
