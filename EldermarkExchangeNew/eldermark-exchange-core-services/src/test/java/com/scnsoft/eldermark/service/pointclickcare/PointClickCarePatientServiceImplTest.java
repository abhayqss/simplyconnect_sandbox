package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ClientValidationViolation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointClickCarePatientServiceImplTest {

    private final static Long ORG_ID = 100L;
    private final static String ORG_UUID = "43241dsfadf21341dsf";
    private final static Long PATIENT_ID = 12L;
    private final static Long PCC_FACILITY_ID = 200L;
    @Mock
    private PointClickCareApiGateway pointClickCareApiGateway;
    @Mock
    private ClientDao clientDao;
    @Mock
    private OrganizationDao organizationDao;
    @Mock
    private CommunityDao communityDao;
    @Mock
    private CcdCodeDao ccdCodeDao;
    @Mock
    private ConcreteCcdCodeDao concreteCcdCodeDao;
    @Mock
    private ClientService clientService;
    @Mock
    private ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;
    @Mock
    private PointClickCareSpecifications pccSpecifications;
    @InjectMocks
    private PointClickCarePatientServiceImpl instance;

    private PCCPatientDetails createPccPatient() {
        var pccPatient = new PCCPatientDetails();
        pccPatient.setFacId(PCC_FACILITY_ID);
        pccPatient.setOrgUuid(ORG_UUID);
        return pccPatient;
    }

    @Nested
    public class ViolatingFieldsCleanupTest {

        @Test
        void emailIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setEmail("asdffdsa@fff.com");

            mockCommon(pccPatient, Set.of());

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getPerson().getTelecoms()).allMatch(telecom ->
                    telecom.getValue().equals("asdffdsa@fff.com") &&
                            telecom.getUseCode().equals(PersonTelecomCode.EMAIL.name()));
        }

        @Test
        void duplicateEmailNotSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setEmail("asdffdsa@fff.com");

            mockCommon(pccPatient, Set.of(ClientValidationViolation.EMAIL));

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getPerson().getTelecoms()).isEmpty();
        }

        @Test
        void medicareNumberIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setMedicareNumber("1fsdfsdf");

            mockCommon(pccPatient, Set.of());

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getMedicareNumber()).isEqualTo("1fsdfsdf");
        }

        @Test
        void duplicateMedicareNumberNotSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setMedicareNumber("1fsdfsdf");

            mockCommon(pccPatient, Set.of(ClientValidationViolation.MEDICARE_NUMBER));

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getMedicareNumber()).isNull();
        }

        @Test
        void medicaidNumberIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setMedicaidNumber("1fsdfsdff");

            mockCommon(pccPatient, Set.of());

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getMedicaidNumber()).isEqualTo("1fsdfsdff");
        }

        @Test
        void duplicateMedicaidNumberNotSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setMedicareNumber("1fsdfsdff");

            mockCommon(pccPatient, Set.of(ClientValidationViolation.MEDICAID_NUMBER));

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getMedicaidNumber()).isNull();
        }


        @Test
        void SSNIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setSocialBeneficiaryIdentifier("123-12-4321");

            mockCommon(pccPatient, Set.of());

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getSocialSecurity()).isEqualTo("123124321");
        }

        @Test
        void duplicateSSNNotSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setSocialBeneficiaryIdentifier("123-12-4321");

            mockCommon(pccPatient, Set.of(ClientValidationViolation.SSN));

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getSocialSecurity()).isNull();
        }

        @Test
        void dobIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setBirthDate(LocalDate.of(1991, 11, 4));

            mockCommon(pccPatient, Set.of());

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getBirthDate()).isEqualTo(LocalDate.of(1991, 11, 4));
        }

        @Test
        void dobInFutureIsSaved() {
            var pccPatient = createPccPatient();
            pccPatient.setBirthDate(LocalDate.of(1991, 11, 4));

            mockCommon(pccPatient, Set.of(ClientValidationViolation.BIRTH_DATE));

            var client = instance.createOrUpdateClient(ORG_UUID, PATIENT_ID);

            assertThat(client.getBirthDate()).isNull();
        }

        private void mockCommon(PCCPatientDetails pccPatient, Set<ClientValidationViolation> violations) {
            var patientSpec = (Specification<Client>) Mockito.mock(Specification.class);
            var orgSpec = (Specification<Organization>) Mockito.mock(Specification.class);
            var commSpec = (Specification<Community>) Mockito.mock(Specification.class);

            var org = new Organization();
            org.setId(ORG_ID);
            org.setPccOrgUuid(ORG_UUID);

            var community = new Community();
            community.setOrganization(org);
            community.setOrganizationId(org.getId());
            community.setPccFacilityId(PCC_FACILITY_ID);
            community.setPccFacilityCountry("USA");

            when(pointClickCareApiGateway.patientById(ORG_UUID, PATIENT_ID)).thenReturn(pccPatient);
            when(pccSpecifications.clientByPccOrgUuidAndPccPatientId(ORG_UUID, PATIENT_ID)).thenReturn(patientSpec);
            when(clientDao.findFirst(patientSpec, Client.class)).thenReturn(Optional.empty());
            when(pccSpecifications.orgByPccOrgUuid(ORG_UUID)).thenReturn(orgSpec);
            when(organizationDao.findFirst(orgSpec, Organization.class)).thenReturn(Optional.of(org));
            when(pccSpecifications.comunityByOrgIdAndPccFacilityId(ORG_ID, PCC_FACILITY_ID)).thenReturn(commSpec);
            when(communityDao.findFirst(commSpec, Community.class)).thenReturn(Optional.of(community));

            when(clientService.runValidation(any(), eq(false))).thenReturn(violations);

            doNothing().when(clientHieConsentDefaultPolicyService).fillDefaultPolicy(any());

            doAnswer(returnsFirstArg()).when(clientService).save(any());
        }

    }


}