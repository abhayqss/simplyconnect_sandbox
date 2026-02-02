package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPartnersAdjustmentRxClaimProcessorImplTest {

    @Mock
    private MedicationService medicationService;

    @Mock
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @InjectMocks
    private HealthPartnersAdjustmentRxClaimProcessorImpl instance;

    @Test
    void process_adjustmentClaimOriginalNotFound_throws() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);

        var originalClaimNo = "441111111";
        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        var thrown = assertThrows(ValidationException.class,
                () -> instance.processAdjustmentRxClaim(null, claim, ctx));

        assertThat(thrown.getMessage()).contains("Original claim is missing");

        verifyNoInteractions(medicationService);
        verifyNoInteractions(healthPartnersRxClaimDao);
    }

    @Test
    void process_adjustmentClaimClientNotMatches_throws() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier("1144141411");

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        var thrown = assertThrows(ValidationException.class,
                () -> instance.processAdjustmentRxClaim(originalClaim, claim, ctx));

        assertThat(thrown.getMessage()).contains("Original and adjustment member identifier didn't match");

        verifyNoInteractions(medicationService);
        verifyNoInteractions(healthPartnersRxClaimDao);
    }

    @Test
    void process_adjustmentClaimNDCNotMatches_throws() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setNationalDrugCode("1");

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier(memberIdentifier);
        originalClaim.setNationalDrugCode("2");

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        var thrown = assertThrows(ValidationException.class,
                () -> instance.processAdjustmentRxClaim(originalClaim, claim, ctx));

        assertThat(thrown.getMessage()).contains("Original and adjustment national drug code didn't match");

        verifyNoInteractions(medicationService);
        verifyNoInteractions(healthPartnersRxClaimDao);
    }

    @Test
    void process_adjustmentQtyQualifierNotMatches_throws() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setNationalDrugCode("1");
        claim.setQuantityQualifierCode("a");

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier(memberIdentifier);
        originalClaim.setNationalDrugCode("1");
        originalClaim.setQuantityQualifierCode("b");

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        var thrown = assertThrows(ValidationException.class,
                () -> instance.processAdjustmentRxClaim(originalClaim, claim, ctx));

        assertThat(thrown.getMessage()).contains("Original and adjustment quantity qualifier code didn't match");

        verifyNoInteractions(medicationService);
        verifyNoInteractions(healthPartnersRxClaimDao);
    }


    @Test
    void process_adjustmentResultNotZeroQtyAndDS_dispenseUpdated() {
        test_dispenseUpdated(BigDecimal.valueOf(2L), BigDecimal.valueOf(-1L), 3, -2);
    }

    @Test
    void process_adjustmentResultZeroQtyNonZeroDS_dispenseUpdated() {
        test_dispenseUpdated(BigDecimal.valueOf(2L), BigDecimal.valueOf(-2L), 3, -2);
    }

    @Test
    void process_adjustmentResultNotZeroQtyZeroDS_dispenseUpdated() {
        test_dispenseUpdated(BigDecimal.valueOf(2L), BigDecimal.valueOf(-1L), 3, -3);
    }

    void test_dispenseUpdated(BigDecimal dispenseQuantity, BigDecimal adjustedQuantity,
                              Integer dispenseDaysSupply, Integer adjustedDaysSupply) {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setNationalDrugCode("1");

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier(memberIdentifier);
        originalClaim.setNationalDrugCode(claim.getNationalDrugCode());

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        claim.setQuantityDispensed(adjustedQuantity);
        claim.setDaysSupply(adjustedDaysSupply);
        var dispense = new MedicationDispense();
        dispense.setId(5L);
        dispense.setDispenseDateLow(new Date());
        dispense.setDispenseDateHigh(new Date(dispense.getDispenseDateLow().getTime() + Duration.ofDays(dispenseDaysSupply).toMillis()));
        dispense.setQuantity(dispenseQuantity);
        var medication = new Medication();
        bind(medication, dispense);

        originalClaim.setMedicationDispense(dispense);
        originalClaim.setMedicationDispenseId(dispense.getId());

        when(medicationService.save(medication)).thenReturn(medication);


        instance.processAdjustmentRxClaim(originalClaim, claim, ctx);

        assertThat(claim.getMedicationDispense()).isSameAs(dispense);
        assertThat(claim.getMedicationDispenseId()).isSameAs(dispense.getId());

        assertThat(dispense.getQuantity()).isEqualTo(dispenseQuantity.add(adjustedQuantity));
        assertThat(dispense.getDispenseDateHigh().toInstant()).isEqualTo(
                dispense.getDispenseDateLow().toInstant()
                        .plus(Duration.ofDays(dispenseDaysSupply + adjustedDaysSupply)));

        verifyNoMoreInteractions(medicationService);
        verifyNoInteractions(healthPartnersRxClaimDao);
    }

    @Test
    void process_adjustmentResultZeroQtyZeroDSMultipleDispenses_dispenseDeleted() {
        var dispenseQuantity = BigDecimal.valueOf(2L);
        var dispenseDaysSupply = 6;

        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setNationalDrugCode("1");

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier(memberIdentifier);
        originalClaim.setNationalDrugCode(claim.getNationalDrugCode());

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        claim.setQuantityDispensed(dispenseQuantity.negate());
        claim.setDaysSupply(-dispenseDaysSupply);

        var dispense = new MedicationDispense();
        dispense.setId(5L);
        dispense.setDispenseDateLow(new Date());
        dispense.setDispenseDateHigh(new Date(dispense.getDispenseDateLow().getTime() + Duration.ofDays(dispenseDaysSupply).toMillis()));
        dispense.setQuantity(dispenseQuantity);
        var medication = new Medication();
        bind(medication, dispense);

        var dispense2 = new MedicationDispense();
        dispense2.setId(6L);
        bind(medication, dispense2);

        originalClaim.setMedicationDispense(dispense);
        originalClaim.setMedicationDispenseId(dispense.getId());

        when(medicationService.save(medication)).thenReturn(medication);

        instance.processAdjustmentRxClaim(originalClaim, claim, ctx);

        assertThat(claim.getMedicationDispense()).isNull();
        assertThat(claim.getMedicationDispenseId()).isNull();
        assertThat(claim.getMedicationDeletedType())
                .isEqualTo(HealthPartnersUtils.ADJUSTMENT_CAUSED_DISPENSE_DELETE);

        assertThat(medication.getMedicationDispenses()).containsExactly(dispense2);


        verify(healthPartnersRxClaimDao).setMedicationDispenseNull(
                HealthPartnersUtils.DISPENSE_DELETED_BY_ADJUSTMENT,
                dispense.getId()
        );
        verifyNoMoreInteractions(medicationService);
    }

    @Test
    void process_adjustmentResultZeroQtyZeroDSOneDispense_MedicationDeleted() {
        var dispenseQuantity = BigDecimal.valueOf(2L);
        var dispenseDaysSupply = 6;

        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setNationalDrugCode("1");

        var originalClaimNo = "441111111";
        var originalClaim = new HealthPartnersRxClaim();
        originalClaim.setClaimNo(originalClaimNo);
        originalClaim.setMemberIdentifier(memberIdentifier);
        originalClaim.setNationalDrugCode(claim.getNationalDrugCode());

        claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        claim.setQuantityDispensed(dispenseQuantity.negate());
        claim.setDaysSupply(-dispenseDaysSupply);

        var dispense = new MedicationDispense();
        dispense.setId(5L);
        dispense.setDispenseDateLow(new Date());
        dispense.setDispenseDateHigh(new Date(dispense.getDispenseDateLow().getTime() + Duration.ofDays(dispenseDaysSupply).toMillis()));
        dispense.setQuantity(dispenseQuantity);
        var medication = new Medication();
        bind(medication, dispense);

        originalClaim.setMedicationDispense(dispense);
        originalClaim.setMedicationDispenseId(dispense.getId());

        instance.processAdjustmentRxClaim(originalClaim, claim, ctx);

        assertThat(claim.getMedicationDispense()).isNull();
        assertThat(claim.getMedicationDispenseId()).isNull();
        assertThat(claim.getMedicationDeletedType())
                .isEqualTo(HealthPartnersUtils.ADJUSTMENT_CAUSED_MEDICATION_DELETE);


        verify(healthPartnersRxClaimDao).setMedicationDispenseNull(
                HealthPartnersUtils.MEDICATION_DELETED_BY_ADJUSTMENT,
                dispense.getId()
        );
        verify(medicationService).delete(medication);
        verifyNoMoreInteractions(medicationService);
    }

    private void bind(Medication medication, MedicationDispense dispense) {
        if (medication.getMedicationDispenses() == null) {
            medication.setMedicationDispenses(new ArrayList<>());
        }
        medication.getMedicationDispenses().add(dispense);
        dispense.setMedication(medication);
    }
}