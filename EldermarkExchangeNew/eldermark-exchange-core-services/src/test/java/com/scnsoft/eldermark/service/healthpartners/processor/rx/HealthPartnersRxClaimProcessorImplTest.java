package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.healthpartners.client.HpClaimClientProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPartnersRxClaimProcessorImplTest {

    private static final Long COMMUNITY_ID = 154231L;

    @Mock
    private HpClaimClientProvider hpClaimClientProvider;

    @Mock
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Mock
    private HealthPartnersNormalRxClaimProcessor normalRxClaimProcessor;

    @Mock
    private HealthPartnersAdjustmentRxClaimProcessor adjustmentRxClaimProcessor;

    @InjectMocks
    private HealthPartnersRxClaimProcessorImpl instance;

    @Test
    void process_getClientThrows_successIsFalse() {
        var claim = new HealthPartnersRxClaim();
        var errorMsg = "get client exception";

        doThrow(new RuntimeException(errorMsg)).when(hpClaimClientProvider).getClient(
                eq(claim), eq(COMMUNITY_ID), any());

        instance.process(claim, COMMUNITY_ID);

        assertFalse(claim.isSuccess());
        assertNotNull(claim.getErrorMessage());

        verifyNoInteractions(healthPartnersRxClaimDao);
        verifyNoInteractions(normalRxClaimProcessor);
        verifyNoInteractions(adjustmentRxClaimProcessor);
    }

    @Test
    void process_recordIsDuplicate_successIsTrue() {
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        var hpFileLogId = 3L;

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setHpFileLogId(hpFileLogId);

        var client = new Client();

        when(hpClaimClientProvider.getClient(eq(claim), eq(COMMUNITY_ID), any())).thenReturn(client);
        when(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                memberIdentifier, claimNo, hpFileLogId
        )).thenReturn(true);

        instance.process(claim, COMMUNITY_ID);

        assertTrue(claim.isSuccess());
        assertNull(claim.getErrorMessage());
        assertTrue(claim.getDuplicate());
        assertNull(claim.getMedicationDispenseId());
        assertNull(claim.getMedicationDispense());

        verifyNoInteractions(normalRxClaimProcessor);
        verifyNoInteractions(adjustmentRxClaimProcessor);
    }

    @Test
    void process_AdjustmentIdentifierEmpty_ProcessedAsNormalSuccessIsTrue() {
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        var hpFileLogId = 3L;

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setHpFileLogId(hpFileLogId);

        var client = new Client();

        when(hpClaimClientProvider.getClient(eq(claim), eq(COMMUNITY_ID), any())).thenReturn(client);
        when(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                memberIdentifier, claimNo, hpFileLogId
        )).thenReturn(false);

        instance.process(claim, COMMUNITY_ID);

        assertTrue(claim.isSuccess());
        assertNull(claim.getErrorMessage());
        assertFalse(claim.getDuplicate());

        verify(normalRxClaimProcessor).processNormalRxClaim(eq(claim), any(), eq(client));
        verifyNoInteractions(adjustmentRxClaimProcessor);
    }

    @ParameterizedTest
    @MethodSource("nonNegativeQtyDaySupply")
    void process_OriginalClaimNotFoundNonNegativeQtyDaySupply_ProcessedAsNormalSuccessIsTrue(
            BigDecimal qty, Integer daysSupply) {
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        var hpFileLogId = 3L;
        var originalClaimNo = "44444444";

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setHpFileLogId(hpFileLogId);
        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        claim.setQuantityDispensed(qty);
        claim.setDaysSupply(daysSupply);

        var client = new Client();

        when(hpClaimClientProvider.getClient(eq(claim), eq(COMMUNITY_ID), any())).thenReturn(client);
        when(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                memberIdentifier, claimNo, hpFileLogId
        )).thenReturn(false);
        when(healthPartnersRxClaimDao.findFirstByClaimNoAndIsSuccessTrueAndMedicationDispenseIsNotNull(originalClaimNo))
                .thenReturn(null);

        instance.process(claim, COMMUNITY_ID);

        assertTrue(claim.isSuccess());
        assertNull(claim.getErrorMessage());
        assertFalse(claim.getDuplicate());

        verify(normalRxClaimProcessor).processNormalRxClaim(eq(claim), any(), eq(client));
        verifyNoInteractions(adjustmentRxClaimProcessor);
    }

    private static Stream<Arguments> nonNegativeQtyDaySupply() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(5L), 2),
                Arguments.of(BigDecimal.valueOf(5L), null),
                Arguments.of(null, 2),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("anyNegativeQtyDaySupply")
    void process_OriginalClaimNotFoundAnyNegativeQtyDaySupply_ProcessedAsAdjustmentSuccessIsTrue(
            BigDecimal qty, Integer daysSupply) {
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        var hpFileLogId = 3L;
        var originalClaimNo = "44444444";

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setHpFileLogId(hpFileLogId);
        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        claim.setQuantityDispensed(qty);
        claim.setDaysSupply(daysSupply);

        var client = new Client();

        when(hpClaimClientProvider.getClient(eq(claim), eq(COMMUNITY_ID), any())).thenReturn(client);
        when(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                memberIdentifier, claimNo, hpFileLogId
        )).thenReturn(false);
        when(healthPartnersRxClaimDao.findFirstByClaimNoAndIsSuccessTrueAndMedicationDispenseIsNotNull(originalClaimNo))
                .thenReturn(null);

        instance.process(claim, COMMUNITY_ID);

        assertTrue(claim.isSuccess());
        assertNull(claim.getErrorMessage());
        assertFalse(claim.getDuplicate());

        verify(adjustmentRxClaimProcessor).processAdjustmentRxClaim(eq(null), eq(claim), any());
        verifyNoInteractions(normalRxClaimProcessor);
    }

    private static Stream<Arguments> anyNegativeQtyDaySupply() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-5L), -2),
                Arguments.of(BigDecimal.valueOf(-5L), null),
                Arguments.of(null, -2)
        );
    }

    @ParameterizedTest
    @MethodSource("allQtyDaySupply")
    void process_OriginalClaimFound_ProcessedAsAdjustmentSuccessIsTrue(
            BigDecimal qty, Integer daysSupply) {
        var claim = new HealthPartnersRxClaim();
        var memberIdentifier = "123444";
        var claimNo = "43421234";
        var hpFileLogId = 3L;
        var originalClaimNo = "44444444";
        var originalClaim = new HealthPartnersRxClaim();

        claim.setMemberIdentifier(memberIdentifier);
        claim.setClaimNo(claimNo);
        claim.setHpFileLogId(hpFileLogId);
        claim.setClaimAdjustedFromIdentifier(originalClaimNo);

        claim.setQuantityDispensed(qty);
        claim.setDaysSupply(daysSupply);

        var client = new Client();

        when(hpClaimClientProvider.getClient(eq(claim), eq(COMMUNITY_ID), any())).thenReturn(client);
        when(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                memberIdentifier, claimNo, hpFileLogId
        )).thenReturn(false);
        when(healthPartnersRxClaimDao.findFirstByClaimNoAndIsSuccessTrueAndMedicationDispenseIsNotNull(originalClaimNo))
                .thenReturn(originalClaim);

        instance.process(claim, COMMUNITY_ID);

        assertTrue(claim.isSuccess());
        assertNull(claim.getErrorMessage());
        assertFalse(claim.getDuplicate());

        verify(adjustmentRxClaimProcessor).processAdjustmentRxClaim(eq(originalClaim), eq(claim), any());
        verifyNoInteractions(normalRxClaimProcessor);
    }

    private static Stream<Arguments> allQtyDaySupply() {
        return Stream.concat(
                anyNegativeQtyDaySupply(),
                nonNegativeQtyDaySupply()
        );
    }
}