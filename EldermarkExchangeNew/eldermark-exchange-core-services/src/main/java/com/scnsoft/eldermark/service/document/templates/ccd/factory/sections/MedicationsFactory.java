package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.parser.entries.SectionEntryParseFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.SubstanceAdministration;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.MedicationsSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

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
@Component
public class MedicationsFactory extends RequiredTemplateFactory
        implements ParsableSectionFactory<MedicationsSection, Medication> {

    public static final String LEGACY_TABLE = "Medication_NWHIN";
    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    @Override
    public MedicationsSection buildTemplateInstance(Collection<Medication> medications) {
        final MedicationsSection section = CCDFactory.eINSTANCE.createMedicationsSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.8"));

        final CE sectionCode = CcdUtils.createCE("10160-0", "HISTORY OF MEDICATION USE", CodeSystem.LOINC);
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Medications");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(medications));

        if (medications == null || medications.isEmpty()) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setSubstanceAdministration(ccdSectionEntryFactory.buildNullMedicationActivity());
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
                    ccdSectionEntryFactory.buildMedicationActivity(medication, entriesReferredToSectionText));
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

    @Override
    public List<Medication> parseSection(Client client, MedicationsSection medicationsSection) {
        if (!CcdParseUtils.hasContent(medicationsSection)
                || CollectionUtils.isEmpty(medicationsSection.getSubstanceAdministrations())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Medication> medications = new ArrayList<>();
        for (SubstanceAdministration substanceAdministration : medicationsSection.getSubstanceAdministrations()) {
            final Medication medication = sectionEntryParseFactory.parseMedicationActivity(substanceAdministration,
                    client, LEGACY_TABLE);
            if (medication != null) {
                medications.add(medication);
            }
        }

        return medications;
    }

}
