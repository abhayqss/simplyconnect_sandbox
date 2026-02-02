package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import com.scnsoft.eldermark.service.healthpartners.medication.HpClaimMedicationProvider;
import com.scnsoft.eldermark.service.healthpartners.medication.HpMedicationDispenseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
class HealthPartnersNormalRxClaimProcessorImpl implements HealthPartnersNormalRxClaimProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersNormalRxClaimProcessorImpl.class);

    @Autowired
    private HpClaimMedicationProvider hpClaimMedicationProvider;

    @Autowired
    private HpMedicationDispenseFactory hpMedicationDispenseFactory;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private NDCToMedicationAppenderImpl ndcToMedicationAppender;

    @Override
    public void processNormalRxClaim(HealthPartnersRxClaim claim, RxClaimProcessingContext ctx, IdOrganizationIdActiveAware client) {
        claim.setAdjustment(false);
        var medication = hpClaimMedicationProvider.getMedication(client, claim, ctx);
        ndcToMedicationAppender.addUniqueNdcToMedication(medication, claim.getNationalDrugCode(), claim.getDrugName());
        addMedicationDispense(claim, client, medication, ctx);
        medicationService.save(medication);

        var createdDispense = findCreatedDispense(medication, ctx);
        claim.setMedicationDispense(createdDispense);
        claim.setMedicationDispenseId(createdDispense.getId());
    }

    private MedicationDispense findCreatedDispense(Medication medication, RxClaimProcessingContext ctx) {
        return medication.getMedicationDispenses().stream()
                .filter(d -> !ctx.getExistedMedicationDispenses().contains(d.getId()))
                .findFirst()
                .orElseThrow();
    }

    private void addMedicationDispense(HealthPartnersRxClaim claim, IdOrganizationIdActiveAware client, Medication medication,
                                       RxClaimProcessingContext ctx) {
        if (medication.getMedicationDispenses() == null) {
            medication.setMedicationDispenses(new ArrayList<>());
        }

        ctx.setExistedMedicationDispenses(medication.getMedicationDispenses()
                .stream()
                .map(BasicEntity::getId)
                .collect(Collectors.toList())
        );

        var newDispense = hpMedicationDispenseFactory.create(claim, client);
        medication.getMedicationDispenses().add(newDispense);
        newDispense.setMedication(medication);

        ctx.getUpdateTypes().add(ResidentUpdateType.MEDICATION);
    }
}
