package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Collection;
import java.util.Optional;

/**
 * <h1>Medical Equipment</h1> “All pertinent equipment relevant to the
 * diagnosis, care, and treatment of a patient should be included.” [CCD 3.10]
 *
 * @see MedicalEquipment
 * @see Client
 */
@Component("consol.MedicalEquipmentFactory")
public class MedicalEquipmentFactory extends OptionalTemplateFactory
        implements SectionFactory<MedicalEquipmentSection, MedicalEquipment> {

    public static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.23";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

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
            entry.setSupply(consolSectionEntryFactory.buildNonMedicalActivity(medicalEquipment));
            section.getEntries().add(entry);
        }

        return section;
    }

    private static String buildSectionText(Collection<MedicalEquipment> medicalEquipmentList) {

        if (CollectionUtils.isEmpty(medicalEquipmentList)) {
            return "No known medical equipment.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Supply/Device</th>" +
                "<th>Date Supplied</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (MedicalEquipment medicalEquipment : medicalEquipmentList) {
            body.append("<tr>" +
                    "<td>");
            if (medicalEquipment.getProductInstance() != null
                    && medicalEquipment.getProductInstance().getDeviceCode() != null
                    && !StringUtils.isEmpty(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName())) {
                body.append(StringEscapeUtils
                        .escapeHtml(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName()));
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            CcdUtils.addDateCell(medicalEquipment.getEffectiveTimeHigh(), body);

            body.append("</tr>");
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
    }

}
