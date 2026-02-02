package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
class HealthPartnersAdjustmentRxClaimProcessorImpl implements HealthPartnersAdjustmentRxClaimProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersAdjustmentRxClaimProcessorImpl.class);

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Override
    public void processAdjustmentRxClaim(HealthPartnersRxClaim originalClaim, HealthPartnersRxClaim adjustmentClaim, RxClaimProcessingContext ctx) {
        adjustmentClaim.setAdjustment(true);

        validateAdjustmentClaim(originalClaim, adjustmentClaim);

        var originalDispense = originalClaim.getMedicationDispense();

        adjustDispenseFields(originalDispense, adjustmentClaim);

        if (shouldDeleteDispense(originalDispense)) {
            String adjustmentDeleteType;
            if (shouldDeleteMedication(originalDispense)) {
                deleteMedication(originalDispense);
                adjustmentDeleteType = HealthPartnersUtils.ADJUSTMENT_CAUSED_MEDICATION_DELETE;
            } else {
                deleteMedicationDispense(originalDispense);
                adjustmentDeleteType = HealthPartnersUtils.ADJUSTMENT_CAUSED_DISPENSE_DELETE;
            }

            adjustmentClaim.setMedicationDeletedType(adjustmentDeleteType);
        } else {
            logger.info("Adjusted dispense {}", originalDispense.getId());
            medicationService.save(originalDispense.getMedication());

            adjustmentClaim.setMedicationDispense(originalDispense);
            adjustmentClaim.setMedicationDispenseId(originalDispense.getId());
        }

        ctx.getUpdateTypes().add(ResidentUpdateType.MEDICATION);
    }

    private void validateAdjustmentClaim(HealthPartnersRxClaim originalClaim, HealthPartnersRxClaim adjustmentClaim) {
        if (originalClaim == null) {
            throw new ValidationException("Original claim is missing");
        }

        var errors = new ArrayList<String>();

        if (!adjustmentClaim.getMemberIdentifier().equals(originalClaim.getMemberIdentifier())) {
            errors.add("Original and adjustment member identifier didn't match");
        }

        if (!Objects.equals(adjustmentClaim.getNationalDrugCode(), originalClaim.getNationalDrugCode())) {
            errors.add("Original and adjustment national drug code didn't match");
        }

        if (!Objects.equals(adjustmentClaim.getQuantityQualifierCode(), originalClaim.getQuantityQualifierCode())) {
            errors.add("Original and adjustment quantity qualifier code didn't match");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void adjustDispenseFields(MedicationDispense originalDispense, HealthPartnersRxClaim adjustmentClaim) {
        if (originalDispense.getQuantity() != null && adjustmentClaim.getQuantityDispensed() != null) {
            originalDispense.setQuantity(originalDispense.getQuantity().add(adjustmentClaim.getQuantityDispensed()));
        }

        if (originalDispense.getDispenseDateHigh() != null && adjustmentClaim.getDaysSupply() != null) {
            originalDispense.setDispenseDateHigh(DateTimeUtils.toDate(originalDispense.getDispenseDateHigh().toInstant().plus(adjustmentClaim.getDaysSupply(), ChronoUnit.DAYS)));
        }
    }

    private boolean shouldDeleteDispense(MedicationDispense originalDispense) {
        return originalDispense.getQuantity() != null &&
                originalDispense.getQuantity().compareTo(BigDecimal.ZERO) == 0 &&
                originalDispense.getDispenseDateHigh() != null &&
                originalDispense.getDispenseDateHigh().equals(originalDispense.getDispenseDateLow());
    }

    private boolean shouldDeleteMedication(MedicationDispense originalDispense) {
        return originalDispense.getMedication().getMedicationDispenses().size() == 1;
    }


    private void deleteMedication(MedicationDispense originalDispense) {
        logger.info("Will delete medication {}", originalDispense.getMedication().getId());
        healthPartnersRxClaimDao.setMedicationDispenseNull(HealthPartnersUtils.MEDICATION_DELETED_BY_ADJUSTMENT,
                originalDispense.getId()
        );

        medicationService.delete(originalDispense.getMedication());
    }

    private void deleteMedicationDispense(MedicationDispense originalDispense) {
        logger.info("Will delete medication dispense {}", originalDispense.getId());
        healthPartnersRxClaimDao.setMedicationDispenseNull(HealthPartnersUtils.DISPENSE_DELETED_BY_ADJUSTMENT,
                originalDispense.getId()
        );

        var medication = originalDispense.getMedication();
        medication.getMedicationDispenses().remove(originalDispense);
        originalDispense.setMedication(null);

        medicationService.save(medication);
    }
}

