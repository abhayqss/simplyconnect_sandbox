package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import com.scnsoft.eldermark.service.healthpartners.medication.HpClaimMedicationProvider;
import com.scnsoft.eldermark.service.healthpartners.medication.HpMedicationDispenseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthPartnersNormalRxClaimProcessorImplTest {

    @Mock
    private HpClaimMedicationProvider hpClaimMedicationProvider;

    @Mock
    private HpMedicationDispenseFactory hpMedicationDispenseFactory;

    @Mock
    private MedicationService medicationService;

    @Mock
    private NDCToMedicationAppenderImpl ndcToMedicationAppender;

    @InjectMocks
    private HealthPartnersNormalRxClaimProcessorImpl instance;

    @Test
    void process_getMedicationThrows_processingStops() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();

        var errorMsg = "get medication exception";
        var client = new Client();

        var exception = new RuntimeException(errorMsg);
        doThrow(exception).when(hpClaimMedicationProvider).getMedication(client, claim, ctx);

        var thrown = assertThrows(RuntimeException.class, () -> instance.processNormalRxClaim(claim, ctx, client));

        assertSame(exception, thrown);

        verifyNoInteractions(hpMedicationDispenseFactory);
        verifyNoInteractions(medicationService);
    }

    @Test
    void process_medicationNdcAppenderThrows_processingStops() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var ndc = "12345";
        var drugName = "drugName";
        claim.setNationalDrugCode(ndc);
        claim.setDrugName(drugName);

        var client = new Client();
        var medication = new Medication();

        var errorMsg = "ndc appender exception";
        var exception = new RuntimeException(errorMsg);

        when(hpClaimMedicationProvider.getMedication(client, claim, ctx)).thenReturn(medication);
        doThrow(exception).when(ndcToMedicationAppender).addUniqueNdcToMedication(medication, ndc, drugName);

        var thrown = assertThrows(RuntimeException.class, () -> instance.processNormalRxClaim(claim, ctx, client));

        assertSame(exception, thrown);

        verifyNoInteractions(medicationService);
    }

    @Test
    void process_medicationDispenseFactoryThrows_processingStops() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var ndc = "12345";
        var drugName = "drugName";
        claim.setNationalDrugCode(ndc);
        claim.setDrugName(drugName);

        var errorMsg = "create medication dispense exception";
        var exception = new RuntimeException(errorMsg);

        var client = new Client();
        var medication = new Medication();

        when(hpClaimMedicationProvider.getMedication(client, claim, ctx)).thenReturn(medication);
        doThrow(exception).when(hpMedicationDispenseFactory).create(claim, client);

        var thrown = assertThrows(RuntimeException.class, () -> instance.processNormalRxClaim(claim, ctx, client));

        assertSame(exception, thrown);

        verify(ndcToMedicationAppender).addUniqueNdcToMedication(medication, ndc, drugName);
        verifyNoInteractions(medicationService);
    }

    @Test
    void process_medicationSaveThrows_processingStops() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var ndc = "12345";
        var drugName = "drugName";
        claim.setNationalDrugCode(ndc);
        claim.setDrugName(drugName);

        var errorMsg = "create medication dispense exception";
        var exception = new RuntimeException(errorMsg);

        var client = new Client();
        var medication = new Medication();

        var dispense = new MedicationDispense();


        when(hpClaimMedicationProvider.getMedication(client, claim, ctx)).thenReturn(medication);
        when(hpMedicationDispenseFactory.create(claim, client)).thenReturn(dispense);
        doThrow(exception).when(medicationService).save(medication);

        var thrown = assertThrows(RuntimeException.class, () -> instance.processNormalRxClaim(claim, ctx, client));
        assertSame(exception, thrown);


        verify(ndcToMedicationAppender).addUniqueNdcToMedication(medication, ndc, drugName);
    }

    @Test
    void process_differentNDCExisted_NewAdded() {
        var ctx = new RxClaimProcessingContext();
        var claim = new HealthPartnersRxClaim();
        var ndc = "12345";
        var drugName = "drugName";
        claim.setNationalDrugCode(ndc);
        claim.setDrugName(drugName);

        var clientId = 1L;
        var client = new Client();
        client.setId(clientId);
        var medication = new Medication();
        var dispense = new MedicationDispense();

        when(hpClaimMedicationProvider.getMedication(client, claim, ctx)).thenReturn(medication);
        when(hpMedicationDispenseFactory.create(claim, client)).thenReturn(dispense);
        when(medicationService.save(medication)).thenReturn(medication);

        instance.processNormalRxClaim(claim, ctx, client);

        verify(ndcToMedicationAppender).addUniqueNdcToMedication(medication, ndc, drugName);
    }
}