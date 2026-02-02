package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <h1>Medications</h1> “The Medications section defines a patient’s current
 * medications and pertinent medication history.” [CCD 3.9]
 *
 * @see Medication
 * @see MedicationInformation
 * @see Instructions
 * @see Indication
 * @see Client
 */
@Component("consol.MedicationsFactory")
public class MedicationsFactory extends RequiredTemplateFactory
        implements SectionFactory<MedicationsSection, Medication> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.1.1";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

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
            entry.setSubstanceAdministration(consolSectionEntryFactory.buildNullMedicationActivity());
            section.getEntries().add(entry);
            return section;
        }

        for (Medication medication : medications) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();

            Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
            entriesReferredToSectionText.add(MedicationInformation.class);
            entriesReferredToSectionText.add(Medication.class);
            entriesReferredToSectionText.add(Instructions.class);

            entry.setSubstanceAdministration(
                    consolSectionEntryFactory.buildMedicationActivity(medication, entriesReferredToSectionText));
            section.getEntries().add(entry);
        }

        return section;
    }

    private static String buildSectionText(Collection<Medication> medications) {

        if (CollectionUtils.isEmpty(medications)) {
            return "No known medications.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Medication</th>" +
                "<th>Directions</th>" +
                "<th>Dates</th>" +
                "<th>Status</th>" +
                "<th>Indications</th>" +
                "<th>Fill Instructions</th>" +
                "<th>Frequency</th>" +
                "<th>Recurrence</th>" +
                "<th>Pharmacy</th>" +
                "<th>Dispensing Pharmacy</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Medication medication : medications) {
            body.append("<tr>" +
                    "<td>");
            MedicationInformation medInformation = medication.getMedicationInformation();
            if (medInformation != null) {
                String productName = medInformation.getProductNameText();
                if (productName != null) {
                    CcdUtils.addReferenceToSectionText(
                            MedicationInformation.class.getSimpleName() + medInformation.getId(), productName,
                            body);
                } else {
                    CcdUtils.addEmptyCellToSectionText(body);
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (medication.getFreeTextSig() != null) {
                CcdUtils.addReferenceToSectionText(Medication.class.getSimpleName() + medication.getId(),
                        medication.getFreeTextSig(), body);
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (medication.getMedicationStarted() != null || medication.getMedicationStopped() != null) {
                CcdUtils.addDateRangeToSectionText(medication.getMedicationStarted(), medication.getMedicationStopped(),
                        body);
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (medication.getStatusCode() != null) {
                body.append(StringEscapeUtils.escapeHtml(medication.getStatusCode()));
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            List<Indication> indications = medication.getIndications();
            if (CollectionUtils.isNotEmpty(indications)) {
                String prefix = "";
                for (Indication indication : indications) {
                    if (indication.getValue() != null && indication.getValue().getDisplayName() != null) {
                        body.append(prefix);
                        body.append(StringEscapeUtils.escapeHtml(indication.getValue().getDisplayName()));
                        prefix = "; ";
                    }
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            Instructions instructions = medication.getInstructions();
            if (instructions != null && instructions.getText() != null) {
                CcdUtils.addReferenceToSectionText(Instructions.class.getSimpleName() + instructions.getId(),
                        instructions.getText(), body);
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (Boolean.TRUE.equals(medication.getPrnScheduled())) {
                body.append("Take as needed");
            } else {
                var escapedSchedule = StringEscapeUtils.escapeHtml(medication.getSchedule());
                if (StringUtils.isNotEmpty(escapedSchedule)) {
                    body.append(escapedSchedule);
                } else {
                    CcdUtils.addEmptyCellToSectionText(body);
                }
            }
            body.append("</td>");

            body.append("<td>");
            var eldermarkRecurrence = CcdUtils.parseEldermarkRecurrence(medication.getRecurrence());
            var escapedRecurrence = StringEscapeUtils.escapeHtml(eldermarkRecurrence.map(Objects::toString).orElse(medication.getRecurrence()));
            if (StringUtils.isNotEmpty(escapedRecurrence)) {
                body.append(escapedRecurrence);
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            addMedicationPharmacyText(body, medication.getPharmacy());
            body.append("</td>");

            body.append("<td>");
            addMedicationPharmacyText(body, medication.getDispensingPharmacy());
            body.append("</td>");

            body.append("</tr>");
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
    }


    private static void addMedicationPharmacyText(StringBuilder body, Community community) {
        if (community != null) {
            body.append("<list>");

            body.append("<item>");
            body.append("Code: ").append(StringEscapeUtils.escapeHtml(community.getLegacyId()));
            body.append("</item>");

            body.append("<item>");
            body.append("Name: ").append(StringEscapeUtils.escapeHtml(community.getName()));
            body.append("</item>");

            if (StringUtils.isNotEmpty(community.getPhone())) {
                body.append("<item>");
                body.append("Phone: ").append(StringEscapeUtils.escapeHtml(community.getPhone()));
                body.append("</item>");
            }

            body.append("</list>");
        } else {
            CcdUtils.addEmptyCellToSectionText(body);
        }
    }
}
