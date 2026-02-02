package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import com.scnsoft.eldermark.service.rxnorm.NdcToRxnormResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HpClaimMedicationProviderImpl implements HpClaimMedicationProvider {
    private static final Logger logger = LoggerFactory.getLogger(HpClaimMedicationProviderImpl.class);


    @Autowired
    private NdcToRxnormResolver ndcToRxnormResolver;

    @Autowired
    private HpMedicationFactory hpMedicationFactory;

    @Autowired
    private MedicationService medicationService;

    @Override
    @Transactional
    public Medication getMedication(IdOrganizationIdActiveAware client, HealthPartnersRxClaim claim, RxClaimProcessingContext ctx) {
        var rxNormCode = ndcToRxnormResolver.resolve(claim.getNationalDrugCode()).orElse(null);
        logger.info("Searching for medication");
        var foundMedication = findMedication(client.getId(), claim, ctx, rxNormCode);
        logger.info("Search done");

        return foundMedication.orElseGet(
                () -> hpMedicationFactory.createMedication(claim, rxNormCode, client));
    }

    private Optional<Medication> findMedication(Long clientId, HealthPartnersRxClaim claim, RxClaimProcessingContext ctx,
                                                CcdCode rxNormCode) {
        if (ctx.isClientIsNewHint()) {
            logger.info("won't search for medication - client is new");
            //no need to search in DB if client is new
            return Optional.empty();
        }


        return medicationService.findHealthPartnersMedication(
                clientId,
                rxNormCode,
                claim.getDrugName(),
                claim.getRefillNumber(),
                claim.getPrescriberFirstName(),
                claim.getPrescriberLastName(),
                claim.getPrescribingPhysicianNPI()
        );

    }
}
