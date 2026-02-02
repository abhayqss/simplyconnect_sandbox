package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.parser.entries.SectionEntryParseFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Medical Equipment</h1> “All pertinent equipment relevant to the
 * diagnosis, care, and treatment of a patient should be included.” [CCD 3.10]
 *
 * @see MedicalEquipment
 * @see Client
 */
@Component
public class MedicalEquipmentFactory extends OptionalTemplateFactory
        implements ParsableSectionFactory<MedicalEquipmentSection, MedicalEquipment> {

    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

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

        for (MedicalEquipment medicalEquipment : medicalEquipmentList) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setSupply(ccdSectionEntryFactory.buildNonMedicalActivity(medicalEquipment));
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

    @Override
    public List<MedicalEquipment> parseSection(Client client, MedicalEquipmentSection medicalEquipmentSection) {
        if (!CcdParseUtils.hasContent(medicalEquipmentSection)
                || CollectionUtils.isEmpty(medicalEquipmentSection.getSupplies())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<MedicalEquipment> targetList = new ArrayList<>();
        for (Supply srcSupply : medicalEquipmentSection.getSupplies()) {
            MedicalEquipment targetEquipment = new MedicalEquipment();
            targetList.add(targetEquipment);

            if ((srcSupply
                    .getMoodCode() != null)/* &&(!x_DocumentSubstanceMood.EVN.equals(srcSupply.getMoodCode())) */) {
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
                // expected time in effectiveTimes[0].high, but got time in
                // effectiveTimes[0].center during testing
                targetEquipment.setEffectiveTimeHigh(CcdParseUtils.parseCenterTime((IVL_TS) sxcm_ts));
            }

            if ((CcdParseUtils.hasContent(srcSupply.getQuantity())) && (srcSupply.getQuantity().getValue() != null)) {
                targetEquipment.setQuantity(srcSupply.getQuantity().getValue().intValue());
            }

            for (Participant2 srcParticipant : srcSupply.getParticipants()) {
                if (ParticipationType.PRD.equals(srcParticipant.getTypeCode())
                        || ParticipationType.DEV.equals(srcParticipant.getTypeCode())) {
                    targetEquipment.setProductInstance(
                            sectionEntryParseFactory.parseProductInstance(srcParticipant.getParticipantRole(), client));
                    break;
                }
            }

            targetEquipment.setClient(client);
            targetEquipment.setOrganization(client.getOrganization());
            targetEquipment.setLegacyId(CcdParseUtils.getFirstIdExtension(srcSupply.getIds()));
        }

        return targetList;
    }

}
