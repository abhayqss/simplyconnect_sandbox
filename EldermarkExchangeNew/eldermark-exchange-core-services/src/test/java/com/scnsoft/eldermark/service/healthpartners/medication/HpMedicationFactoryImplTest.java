package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MedicalProfessional;
import com.scnsoft.eldermark.entity.MedicationSupplyOrder;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.service.healthpartners.author.HpMedicalProfessionalAuthorFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpMedicationFactoryImplTest {

    @Mock
    private ClientService clientService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private HpMedicalProfessionalAuthorFactory medicalProfessionalAuthorFactory;

    @InjectMocks
    private HpMedicationFactoryImpl instance;

    private final Author author = new Author();
    private final MedicalProfessional medicalProfessional = new MedicalProfessional();

    @Test
    void createMedication_codeIsPresent_codeIsFilled() {
        var clientId = 1L;
        var client = new Client(clientId);
        var organization = createOrganization();
        client.setOrganization(organization);
        client.setOrganizationId(organization.getId());

        var clientProjection = createClientProjection(clientId, organization.getId(), true);

        var claim = createClaim();
        var rxNormCode = new CcdCode();

        when(organizationService.getOne(organization.getId())).thenReturn(organization);
        when(clientService.getById(clientId)).thenReturn(client);
        when(medicalProfessionalAuthorFactory.create(
                organization,
                HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE,
                claim.getPrescribingPhysicianNPI(), claim.getPrescriberFirstName(),
                claim.getPrescriberMiddleName(),
                claim.getPrescriberLastName())
        )
                .thenReturn(new Pair<>(author, medicalProfessional));

        var actual = instance.createMedication(claim, rxNormCode, clientProjection);

        verifyMedication(actual, client, rxNormCode, claim);
    }

    private IdOrganizationIdActiveAware createClientProjection(Long id, Long orgId, Boolean active) {
        return new IdOrganizationIdActiveAware() {
            @Override
            public Boolean getActive() {
                return active;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public Long getOrganizationId() {
                return orgId;
            }
        };
    }

    @Test
    void createMedication_codeNotPresent_codeNotFilled() {
        var clientId = 1L;
        var client = new Client(clientId);
        var organization = createOrganization();
        client.setOrganization(organization);
        client.setOrganizationId(organization.getId());

        var clientProjection = createClientProjection(clientId, organization.getId(), true);

        var claim = createClaim();

        when(organizationService.getOne(organization.getId())).thenReturn(organization);
        when(clientService.getById(clientId)).thenReturn(client);
        when(medicalProfessionalAuthorFactory.create(
                organization,
                HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE,
                claim.getPrescribingPhysicianNPI(), claim.getPrescriberFirstName(),
                claim.getPrescriberMiddleName(),
                claim.getPrescriberLastName())
        )
                .thenReturn(new Pair<>(author, medicalProfessional));


        var actual = instance.createMedication(claim, null, clientProjection);

        verifyMedication(actual, client, null, claim);
    }


    private HealthPartnersRxClaim createClaim() {
        var claim = new HealthPartnersRxClaim();
        claim.setServiceDate(Instant.now());

        claim.setNationalDrugCode("00071015723");
        claim.setDrugName("drug name");

        claim.setDAWProductSelectionCode("1");
        claim.setPrescriptionOriginCode("3");
        claim.setRXNumber("rxnumber");

        claim.setPrescribingPhysicianNPI("prescriber npi");
        claim.setPrescriberFirstName("firstname");
        claim.setPrescriberMiddleName("middlename");
        claim.setPrescriberLastName("lastname");

        return claim;
    }

    private Organization createOrganization() {
        var org = new Organization();
        org.setId(3L);
        return org;
    }


    private void verifyMedication(Medication medication, Client client, CcdCode rxNormCode, HealthPartnersRxClaim claim) {
        assertEquals(client.getOrganization(), medication.getOrganization());
        assertEquals(claim.getServiceDate().toEpochMilli(), medication.getMedicationStarted().getTime());
        assertEquals(client, medication.getClient());

        verifyMedicationInformation(medication.getMedicationInformation(), client, rxNormCode, claim);
        verifyMedicationSupplyOrder(medication.getMedicationSupplyOrder(), client, claim);
    }

    private void verifyMedicationInformation(MedicationInformation medicationInformation, Client client,
                                             CcdCode rxNormCode, HealthPartnersRxClaim claim) {

        assertEquals(client.getOrganization(), medicationInformation.getOrganization());

        if (rxNormCode == null) {
            assertNull(medicationInformation.getProductNameCode());
            assertEquals(claim.getDrugName(), medicationInformation.getProductNameText());
        } else {
            assertEquals(rxNormCode, medicationInformation.getProductNameCode());
            assertNull(medicationInformation.getProductNameText());
        }
    }

    private void verifyMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder, Client client, HealthPartnersRxClaim claim) {
        assertEquals(client.getOrganization(), medicationSupplyOrder.getOrganization());

        assertEquals(claim.getDAWProductSelectionCode(), medicationSupplyOrder.getDAWProductSelectionCode());
        assertEquals(claim.getPrescriptionOriginCode(), medicationSupplyOrder.getPrescriptionOriginCode());
        assertEquals(claim.getRXNumber(), medicationSupplyOrder.getPrescriptionNumber());

        assertSame(author, medicationSupplyOrder.getAuthor());
        assertSame(medicalProfessional, medicationSupplyOrder.getMedicalProfessional());
    }
}