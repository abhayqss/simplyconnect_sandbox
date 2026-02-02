package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.hl7.datatypes.CS;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.SXCM_TS;
import org.openhealthtools.mdht.uml.cda.consol.MedicalEquipmentSection;
import org.openhealthtools.mdht.uml.cda.consol.NonMedicinalSupplyActivity;
import org.openhealthtools.mdht.uml.cda.consol.ProductInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Medical Equipment</h1> “All pertinent equipment relevant to the
 * diagnosis, care, and treatment of a patient should be included.” [CCD 3.10]
 *
 * @see MedicalEquipment
 * @see Client
 */
@Component("consol.MedicalEquipmentParser")
public class MedicalEquipmentParser
        extends AbstractParsableSection<NonMedicinalSupplyActivity, MedicalEquipmentSection, MedicalEquipment>
        implements ParsableSection<MedicalEquipmentSection, MedicalEquipment> {

    private final ParticipantRoleFactory participantRoleFactory;

    @Autowired
    public MedicalEquipmentParser(ParticipantRoleFactory participantRoleFactory) {
        this.participantRoleFactory = participantRoleFactory;
    }

    @Override
    public boolean isSectionIgnored(MedicalEquipmentSection medicalEquipmentSection) {
        return !CcdParseUtils.hasContent(medicalEquipmentSection)
                || CollectionUtils.isEmpty(medicalEquipmentSection.getNonMedicinalSupplyActivities());
    }

    @Override
    public List<MedicalEquipment> doParseSection(Client resident, MedicalEquipmentSection medicalEquipmentSection) {
        Objects.requireNonNull(resident);

        final List<MedicalEquipment> targetList = new ArrayList<>();
        for (NonMedicinalSupplyActivity srcSupply : medicalEquipmentSection.getNonMedicinalSupplyActivities()) {
            final MedicalEquipment targetEquipment = new MedicalEquipment();

            if ((srcSupply
                    .getMoodCode() != null)/* &&(!x_DocumentSubstanceMood.EVN.equals(srcSupply.getMoodCode())) */) {
                targetEquipment.setMoodCode(srcSupply.getMoodCode().name());
            }

            final CS srcStatusCode = srcSupply.getStatusCode();
            if (CcdParseUtils.hasContent(srcStatusCode)) {
                targetEquipment.setStatusCode(srcStatusCode.getCode());
            }

            final SXCM_TS sxcm_ts = CcdParseUtils.getFirstNotEmptyValue(srcSupply.getEffectiveTimes(), SXCM_TS.class);
            final Pair<Date, Date> effectiveTime = CcdTransform.SXCM_TStoHighLowDate(sxcm_ts);
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

            // TODO test on real examples
            final ProductInstance ccdProductInstance = srcSupply.getProductInstance();
            if (CcdParseUtils.hasContent(ccdProductInstance)) {
                targetEquipment
                        .setProductInstance(participantRoleFactory.parseProductInstance(ccdProductInstance, resident));
            }
            /*
             * for (Participant2 srcParticipant : srcSupply.getParticipants()) { if
             * (ParticipationType.PRD.equals(srcParticipant.getTypeCode()) ||
             * ParticipationType.DEV.equals(srcParticipant.getTypeCode())) {
             * targetEquipment.setProductInstance(participantRoleFactory.
             * parseProductInstance(ccdProductInstance, resident)); break; } }
             */

            targetEquipment.setClient(resident);
            targetEquipment.setOrganization(resident.getOrganization());
            targetEquipment.setLegacyId(CcdParseUtils.getFirstIdExtension(srcSupply.getIds()));

            if (filterMedicalEquipment(targetEquipment)) {
                targetList.add(targetEquipment);
            }
        }
        return targetList;
    }

    private boolean filterMedicalEquipment(MedicalEquipment targetEquipment) {
        return (targetEquipment.getProductInstance() != null
                && targetEquipment.getProductInstance().getDeviceCode() != null
                && StringUtils.isNotEmpty(targetEquipment.getProductInstance().getDeviceCode().getDisplayName()));
    }
}
