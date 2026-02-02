package com.scnsoft.eldermark.consana.sync.client.predicates;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Organization;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaIntegrationEnabledPredicateTest {

    @Mock
    private ResidentService residentService;

    @InjectMocks
    private ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate;

    @Test
    void test_ResidentIsNull_ReturnsFalse() {
        assertFalse(consanaIntegrationEnabledPredicate.test(null));
    }

    @Test
    void test_ConsanaIntegrationForCommunityNotEnabled_ReturnsFalse() {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(false);

        assertFalse(consanaIntegrationEnabledPredicate.test(resident));
    }

    @Test
    void test_NotAdmittedOrPharmacyIncorrect_ReturnsFalse() {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(true);

        when(residentService.isPharmacyNamesAndAdmittedDateCorrect(resident)).thenReturn(false);

        assertFalse(consanaIntegrationEnabledPredicate.test(resident));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_NotNullAndNotOptOutAndConsanaIntegrationForCommunityEnabled_ReturnsTrue(boolean active) {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(true);
        resident.setActive(active);

        when(residentService.isPharmacyNamesAndAdmittedDateCorrect(resident)).thenReturn(true);

        assertTrue(consanaIntegrationEnabledPredicate.test(resident));
    }

    @Test
    void test_ActiveMcFarlandClient_ReturnsTrue() {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(true);
        resident.getFacility().setConsanaOrgId(ConsanaIntegrationEnabledPredicate.MC_FARLAND_CONSANA_ORG_ID);
        resident.setActive(true);

        lenient().when(residentService.isPharmacyNamesAndAdmittedDateCorrect(resident)).thenReturn(false);

        assertTrue(consanaIntegrationEnabledPredicate.test(resident));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_HealthPartnersClient_ReturnsTrue(boolean active) {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(true);
        resident.setHealthPartnersMemberIdentifier("hpIdentifier");
        resident.setActive(active);

        lenient().when(residentService.isPharmacyNamesAndAdmittedDateCorrect(resident)).thenReturn(false);

        assertTrue(consanaIntegrationEnabledPredicate.test(resident));
    }

    @Test
    void test_InactiveNonAdmittedClient_ReturnsFalse() {
        var resident = new Resident();
        resident.setFacility(new Organization());
        resident.getFacility().setConsanaIntegrationEnabled(true);
        resident.getFacility().setConsanaOrgId(ConsanaIntegrationEnabledPredicate.MC_FARLAND_CONSANA_ORG_ID);

        when(residentService.isPharmacyNamesAndAdmittedDateCorrect(resident)).thenReturn(false);

        assertFalse(consanaIntegrationEnabledPredicate.test(resident));
    }
}