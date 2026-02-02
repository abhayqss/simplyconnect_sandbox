package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.MedicalEquipment;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.cda.Supply;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.MedicalEquipmentSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Medical Equipment</h1>
 * “All pertinent equipment relevant to the diagnosis, care, and treatment of a patient should be included.” [CCD 3.10]
 *
 * @see MedicalEquipment
 * @see Resident
 */
@Component
public class MedicalEquipmentFactory extends OptionalTemplateFactory implements ParsableSectionFactory<MedicalEquipmentSection, MedicalEquipment> {

    @Autowired
    SectionEntryParseFactory sectionEntryParseFactory;

    @Value("${section.medicalEquipment.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public MedicalEquipmentSection buildTemplateInstance(Collection<MedicalEquipment> medicalEquipmentList) {
        final MedicalEquipmentSection section = CCDFactory.eINSTANCE.createMedicalEquipmentSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.7"));

        final CE sectionCode = CcdUtils.createCE("46264-8", "History of medical device use", CodeSystem.LOINC);
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Medical Equipment");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(medicalEquipmentList));

        for(MedicalEquipment medicalEquipment : medicalEquipmentList) {
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

        for(MedicalEquipment medicalEquipment : medicalEquipmentList) {
            sectionText.append("<tr>");

            sectionText.append("<td>");
            if(medicalEquipment.getProductInstance() != null && medicalEquipment.getProductInstance().getDeviceCode() != null
                    && !StringUtils.isEmpty(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName())) {
                sectionText.append(StringEscapeUtils.escapeHtml4(medicalEquipment.getProductInstance().getDeviceCode().getDisplayName()));
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            CcdUtils.addDateCell(medicalEquipment.getEffectiveTimeHigh(),sectionText);

            sectionText.append("</tr>");
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public List<MedicalEquipment> parseSection(Resident resident, MedicalEquipmentSection medicalEquipmentSection) {
        if (!CcdParseUtils.hasContent(medicalEquipmentSection) || CollectionUtils.isEmpty(medicalEquipmentSection.getSupplies())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<MedicalEquipment> targetList = new ArrayList<>();
        for (Supply srcSupply : medicalEquipmentSection.getSupplies()) {
            MedicalEquipment targetEquipment = new MedicalEquipment();
            targetList.add(targetEquipment);

            if ((srcSupply.getMoodCode()!=null)/*&&(!x_DocumentSubstanceMood.EVN.equals(srcSupply.getMoodCode()))*/) {
                targetEquipment.setMoodCode(srcSupply.getMoodCode().name());
            }

            CS srcStatusCode = srcSupply.getStatusCode();
            if (CcdParseUtils.hasContent(srcStatusCode)) {
                targetEquipment.setStatusCode(srcStatusCode.getCode());
            }


            final SXCM_TS sxcm_ts = CcdParseUtils.getFirstNotEmptyValue(srcSupply.getEffectiveTimes(), SXCM_TS.class);
            Pair<Date, Date> effectiveTime = CcdTransform.SXCM_TStoHighLowDate(sxcm_ts);
            if (effectiveTime != null) {
                targetEquipment.setEffectiveTimeHigh(effectiveTime.getFirst());
            }
            if (targetEquipment.getEffectiveTimeHigh() == null && sxcm_ts instanceof IVL_TS) {
                // expected time in effectiveTimes[0].high, but got time in effectiveTimes[0].center during testing
                targetEquipment.setEffectiveTimeHigh(CcdParseUtils.parseCenterTime((IVL_TS) sxcm_ts));
            }

            if ((CcdParseUtils.hasContent(srcSupply.getQuantity())) && (srcSupply.getQuantity().getValue()!=null)) {
                targetEquipment.setQuantity(srcSupply.getQuantity().getValue().intValue());
            }

            for (Participant2 srcParticipant : srcSupply.getParticipants()) {
                if (ParticipationType.PRD.equals(srcParticipant.getTypeCode()) || ParticipationType.DEV.equals(srcParticipant.getTypeCode())) {
                    targetEquipment.setProductInstance(sectionEntryParseFactory.parseProductInstance(srcParticipant.getParticipantRole(), resident));
                    break;
                }
            }

            targetEquipment.setResident(resident);
            targetEquipment.setDatabase(resident.getDatabase());
            targetEquipment.setLegacyId(CcdParseUtils.getFirstIdExtension(srcSupply.getIds()));
        }

        return targetList;
    }

}
