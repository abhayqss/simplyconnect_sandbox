package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.MedicalEquipment;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.MedicalEquipmentSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * <h1>Medical Equipment</h1>
 * “All pertinent equipment relevant to the diagnosis, care, and treatment of a patient should be included.” [CCD 3.10]
 *
 * @see MedicalEquipment
 * @see Resident
 */
@Component("consol.MedicalEquipmentFactory")
public class MedicalEquipmentFactory extends OptionalTemplateFactory implements SectionFactory<MedicalEquipmentSection, MedicalEquipment> {

    public static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.23";
    @Value("${section.medicalEquipment.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public MedicalEquipmentSection buildTemplateInstance(Collection<MedicalEquipment> medicalEquipmentList) {
        final MedicalEquipmentSection section = ConsolFactory.eINSTANCE.createMedicalEquipmentSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("46264-8", "History of medical device use", CodeSystem.LOINC);
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Medical Equipment");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(medicalEquipmentList));

        if (CollectionUtils.isEmpty(medicalEquipmentList)) {
            // TODO buildNullMedicalEquipment() ?
            return section;
        }

        for (MedicalEquipment medicalEquipment : medicalEquipmentList) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setSupply(SectionEntryFactory.buildNonMedicalActivity(medicalEquipment));
            section.getEntries().add(entry);
        }

        return section;
    }

    private static String buildSectionText(Collection<MedicalEquipment> medicalEquipmentList) {

        if (CollectionUtils.isEmpty(medicalEquipmentList)) {
            return "No known medical equipment.";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Supply/Device</th>");
        sectionText.append("<th>Date Supplied</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (MedicalEquipment medicalEquipment : medicalEquipmentList) {
            sectionText.append("<tr>");

            sectionText.append("<td>");
            if (medicalEquipment.getProductInstance() != null && medicalEquipment.getProductInstance().getDeviceCode() != null
                    && !StringUtils.isEmpty(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName())) {
                sectionText.append(StringEscapeUtils.escapeHtml4(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName()));
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            CcdUtils.addDateCell(medicalEquipment.getEffectiveTimeHigh(), sectionText);

            sectionText.append("</tr>");
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

}
