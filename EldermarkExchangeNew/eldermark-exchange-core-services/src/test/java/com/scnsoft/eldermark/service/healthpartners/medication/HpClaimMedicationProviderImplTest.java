package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.MedicationService;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import com.scnsoft.eldermark.service.rxnorm.NdcToRxnormResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HpClaimMedicationProviderImplTest {

    @Mock
    private NdcToRxnormResolver ndcToRxnormResolver;

    @Mock
    private HpMedicationFactory hpMedicationFactory;

    @Mock
    private MedicationService medicationService;

    @InjectMocks
    HpClaimMedicationProviderImpl instance;

    @Test
    void getMedication_whenFound_returnsFound() {
        var clientId = 1L;
        var client = new Client(clientId);
        var claim = createClaim();
        var ctx = new RxClaimProcessingContext();
        var rxNormCode = new CcdCode();

        var medication = new Medication();

        when(ndcToRxnormResolver.resolve(claim.getNationalDrugCode())).thenReturn(Optional.of(rxNormCode));
        when(medicationService.findHealthPartnersMedication(
                clientId,
                rxNormCode,
                claim.getDrugName(),
                claim.getRefillNumber(),
                claim.getPrescriberFirstName(),
                claim.getPrescriberLastName(),
                claim.getPrescribingPhysicianNPI())
        )
                .thenReturn(Optional.of(medication));

        instance.getMedication(client, claim, ctx);
    }

    @Test
    void getMedication_whenClientIsNew_callsMedicationsFactoryWithoutSearch() {
        var client = new Client(1L);
        var claim = createClaim();
        var ctx = new RxClaimProcessingContext();
        ctx.setClientIsNewHint(true);
        var rxNormCode = new CcdCode();
        var medication = new Medication();

        when(ndcToRxnormResolver.resolve(claim.getNationalDrugCode())).thenReturn(Optional.of(rxNormCode));
        when(hpMedicationFactory.createMedication(claim, rxNormCode, client)).thenReturn(medication);

        var actual = instance.getMedication(client, claim, ctx);

        assertEquals(medication, actual);

        verify(medicationService, never()).findHealthPartnersMedication(any(), any(), any(), any(),
                any(), any(), any());
    }

    @Test
    void getMedication_whenNotFound_callsMedicationsFactory() {
        var client = new Client(1L);
        var claim = createClaim();
        var ctx = new RxClaimProcessingContext();
        var rxNormCode = new CcdCode();
        var medication = new Medication();


        when(ndcToRxnormResolver.resolve(claim.getNationalDrugCode())).thenReturn(Optional.of(rxNormCode));
        when(medicationService.findHealthPartnersMedication(client.getId(), rxNormCode, claim.getDrugName(),
                claim.getRefillNumber(), claim.getPrescriberFirstName(), claim.getPrescriberLastName(),
                claim.getPrescribingPhysicianNPI())
        )
                .thenReturn(Optional.empty());
        when(hpMedicationFactory.createMedication(claim, rxNormCode, client)).thenReturn(medication);


        var actual = instance.getMedication(client, claim, ctx);


        assertEquals(medication, actual);
    }

    private HealthPartnersRxClaim createClaim() {
        var claim = new HealthPartnersRxClaim();
        claim.setRefillNumber(10);
        claim.setNationalDrugCode("00071015723");
        claim.setDrugName("drug name");
        claim.setPrescriberFirstName("prescriberFN");
        claim.setPrescriberLastName("prescriberLN");
        claim.setPrescribingPhysicianNPI("prescriberNPI");
        return claim;
    }
}